package hgc.flowsyncapi.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hgc.flowsyncapi.dto.AiTaskPlanItem;
import hgc.flowsyncapi.dto.AiTaskPlanResponse;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.service.QwenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QwenServiceImpl implements QwenService {

    private static final Logger log = LoggerFactory.getLogger(QwenServiceImpl.class);

    @Value("${dashscope.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT_TASK_SUGGESTION =
        "你是一个简单直接的项目任务助手。\n" +
        "请用最容易理解的中文输出，给出：\n" +
        "1. 建议拆分的子任务；2. 执行顺序；3. 风险提醒。\n" +
        "控制在300字以内。";

    private static final String SYSTEM_PROMPT_TASK_PLAN =
        "你是一个项目任务拆解助手。\n" +
        "请把大任务拆成可以直接执行的小任务。\n" +
        "我会给你可选的成员名单，请为每个任务推荐一个最合适的负责人，在 assigneeId 字段填写该成员的 id（必须是名单中已有的 id）。\n" +
        "重要：每个任务都必须填写 assigneeId，不能为空；同一个人可以负责多个任务。\n" +
        "只返回严格 JSON，不要解释，不要 markdown。";

    public QwenServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getTaskSuggestion(String projectName, String taskTitle, String taskDescription) {
        String userPrompt = String.format(
            "项目名称：%s\n任务标题：%s\n任务说明：%s\n请给出子任务拆分建议。",
            projectName, taskTitle, taskDescription);

        return callQwen(SYSTEM_PROMPT_TASK_SUGGESTION, userPrompt, 300);
    }

    @Override
    public AiTaskPlanResponse generateTaskPlan(String projectName, String goal,
                                                String description, List<User> members) {
        // 构建成员名单字符串
        StringBuilder memberList = new StringBuilder();
        for (User u : members) {
            memberList.append(u.getId()).append(" - ").append(u.getRealName()).append("\n");
        }

        String userPrompt = String.format(
            "项目名称：%s\n任务目标：%s\n补充说明：%s\n可选成员名单（id - 姓名）：\n%s",
            projectName, goal,
            description != null ? description : "无",
            memberList.toString());

        String rawJson = callQwen(SYSTEM_PROMPT_TASK_PLAN, userPrompt, 2000);

        try {
            AiTaskPlanResponse response = objectMapper.readValue(rawJson, AiTaskPlanResponse.class);

            // 校验负责人 ID 有效性
            Set<Long> validIds = members.stream().map(User::getId).collect(Collectors.toSet());
            Long fallbackId = members.isEmpty() ? null : members.get(0).getId();

            if (response.getItems() != null) {
                for (AiTaskPlanItem item : response.getItems()) {
                    if (item.getAssigneeId() == null || !validIds.contains(item.getAssigneeId())) {
                        item.setAssigneeId(fallbackId);
                    }
                }
            }
            return response;

        } catch (JsonProcessingException e) {
            log.error("千问返回 JSON 解析失败，使用兜底方案。原始返回: {}", rawJson, e);
            return buildFallbackPlan();
        }
    }

    /** 兜底方案 */
    private AiTaskPlanResponse buildFallbackPlan() {
        AiTaskPlanResponse fallback = new AiTaskPlanResponse();
        fallback.setSummary("AI 暂时无法提供服务，以下为系统默认拆解方案，请手动分配负责人。");

        AiTaskPlanItem item1 = new AiTaskPlanItem();
        item1.setTitle("准备资料");
        item1.setDescription("收集和整理项目所需的相关资料与文档");
        item1.setPriority("高");
        item1.setSuggestedDays(2);
        item1.setAssigneeId(null);

        AiTaskPlanItem item2 = new AiTaskPlanItem();
        item2.setTitle("执行主体");
        item2.setDescription("按照计划完成项目核心工作内容");
        item2.setPriority("高");
        item2.setSuggestedDays(5);
        item2.setAssigneeId(null);

        AiTaskPlanItem item3 = new AiTaskPlanItem();
        item3.setTitle("检查总结");
        item3.setDescription("检查完成情况，整理项目总结报告");
        item3.setPriority("中");
        item3.setSuggestedDays(2);
        item3.setAssigneeId(null);

        fallback.setItems(Arrays.asList(item1, item2, item3));
        return fallback;
    }

    /** 调用千问大模型 */
    private String callQwen(String systemPrompt, String userPrompt, int maxTokens) {
        try {
            Generation gen = new Generation();
            Message sysMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(systemPrompt)
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userPrompt)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model("qwen-plus")
                    .messages(Arrays.asList(sysMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .maxTokens(maxTokens)
                    .build();

            GenerationResult result = gen.call(param);
            return result.getOutput().getChoices().get(0).getMessage().getContent().toString();

        } catch (NoApiKeyException | ApiException | InputRequiredException e) {
            log.error("千问调用失败", e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage());
        }
    }
}
