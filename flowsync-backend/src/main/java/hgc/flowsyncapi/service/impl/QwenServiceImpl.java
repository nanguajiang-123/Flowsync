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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hgc.flowsyncapi.dto.AiTaskPlanItem;
import hgc.flowsyncapi.service.QwenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class QwenServiceImpl implements QwenService {

    private static final Logger log = LoggerFactory.getLogger(QwenServiceImpl.class);

    @Value("${dashscope.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    // ---- 优化后的提示词 ----
    private static final String SYSTEM_PROMPT =
        "你是项目管理专家。规则：" +
        "1.任务拆解遵循MECE原则；" +
        "2.颗粒度适中；" +
        "3.taskdesc需含动作与交付物；" +
        "4.严格按用户指定JSON格式输出；" +
        "5.禁止输出JSON以外任何文字、注释、markdown或解释。";

    private static final String SYSTEM_PROMPT_SUGGESTION =
        "你是一个简单直接的项目任务助手。请用最容易理解的中文输出，控制在300字以内。";

    // 姓名 → ID 映射
    private static final Map<String, Long> NAME_TO_ID = new LinkedHashMap<>();
    static {
        NAME_TO_ID.put("项目负责人", 2L);
        NAME_TO_ID.put("张三", 3L);
        NAME_TO_ID.put("李四", 4L);
        NAME_TO_ID.put("系统管理员", 1L);
    }

    public QwenServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getTaskSuggestion(String projectName, String taskTitle, String taskDescription) {
        String userPrompt = String.format(
            "项目名称：%s\n任务标题：%s\n任务说明：%s\n请给出子任务拆分建议。",
            projectName, taskTitle, taskDescription);
        return callQwen(SYSTEM_PROMPT_SUGGESTION, userPrompt, 300);
    }

    @Override
    public List<AiTaskPlanItem> generateTaskPlan(String projectName) {
        String today = LocalDate.now().toString();
        String userPrompt = String.format(
            "拆解%s为JSON数组。字段：projectname(固定值)，taskdesc，assignee(限项目负责人/张三/李四)，" +
            "status(固定未开始),priority(优先级:高、中、低),dueDate(截止日期:yyyy-MM-dd)," +
            "createTime(创建时间:yyyy-MM-dd当前日期为%s)。覆盖全流程，合理分工，仅返回JSON。",
            projectName, today);

        log.info("调用千问拆解: {}", projectName);

        try {
            String raw = callQwen(SYSTEM_PROMPT, userPrompt, 2000);
            String clean = raw.trim().replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            List<AiTaskPlanItem> items = objectMapper.readValue(clean, new TypeReference<List<AiTaskPlanItem>>() {});

            for (AiTaskPlanItem item : items) {
                item.setAssigneeId(NAME_TO_ID.getOrDefault(item.getAssignee(), 2L));
                item.setTitle(item.getTaskdesc());
                item.setDescription(item.getTaskdesc());
                if (item.getDueDate() != null && !item.getDueDate().isEmpty()) {
                    try {
                        long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(item.getDueDate()));
                        item.setSuggestedDays((int) Math.max(1, days));
                    } catch (Exception e) { item.setSuggestedDays(3); }
                } else { item.setSuggestedDays(3); }
            }
            return items;
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败", e);
            return buildFallback(projectName);
        } catch (Exception e) {
            log.error("千问调用异常", e);
            return buildFallback(projectName);
        }
    }

    private List<AiTaskPlanItem> buildFallback(String projectName) {
        String today = LocalDate.now().toString();
        List<AiTaskPlanItem> list = new ArrayList<>();
        AiTaskPlanItem i1 = new AiTaskPlanItem();
        i1.setProjectname(projectName); i1.setTaskdesc("准备资料，梳理需求文档"); i1.setAssignee("项目负责人");
        i1.setStatus("未开始"); i1.setPriority("高"); i1.setDueDate(today); i1.setCreateTime(today);
        i1.setAssigneeId(2L); i1.setTitle(i1.getTaskdesc()); i1.setDescription(i1.getTaskdesc()); i1.setSuggestedDays(2);
        AiTaskPlanItem i2 = new AiTaskPlanItem();
        i2.setProjectname(projectName); i2.setTaskdesc("执行主体任务，完成核心开发"); i2.setAssignee("张三");
        i2.setStatus("未开始"); i2.setPriority("高"); i2.setDueDate(today); i2.setCreateTime(today);
        i2.setAssigneeId(3L); i2.setTitle(i2.getTaskdesc()); i2.setDescription(i2.getTaskdesc()); i2.setSuggestedDays(5);
        AiTaskPlanItem i3 = new AiTaskPlanItem();
        i3.setProjectname(projectName); i3.setTaskdesc("检查总结，整理答辩材料"); i3.setAssignee("李四");
        i3.setStatus("未开始"); i3.setPriority("中"); i3.setDueDate(today); i3.setCreateTime(today);
        i3.setAssigneeId(4L); i3.setTitle(i3.getTaskdesc()); i3.setDescription(i3.getTaskdesc()); i3.setSuggestedDays(2);
        list.add(i1); list.add(i2); list.add(i3);
        return list;
    }

    private String callQwen(String systemPrompt, String userPrompt, int maxTokens) {
        try {
            Generation gen = new Generation();
            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey).model("qwen-plus")
                    .messages(Arrays.asList(
                        Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build(),
                        Message.builder().role(Role.USER.getValue()).content(userPrompt).build()))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .maxTokens(maxTokens).build();
            return gen.call(param).getOutput().getChoices().get(0).getMessage().getContent().toString();
        } catch (NoApiKeyException e) {
            throw new RuntimeException("AI 服务未配置有效的 API Key");
        } catch (ApiException | InputRequiredException e) {
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage());
        }
    }
}
