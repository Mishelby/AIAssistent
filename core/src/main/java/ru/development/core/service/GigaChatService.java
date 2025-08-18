package ru.development.core.service;

import chat.giga.model.completion.Choice;
import chat.giga.model.completion.ChoiceMessage;
import chat.giga.model.completion.CompletionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.development.core.mapper.GigaChatModelInfoMapper;
import ru.development.core.model.GigaChatModelInfo;
import ru.development.core.repository.GigaChatModelInfoRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatService {
    private final GigaChatModelInfoRepository gigaChatModelInfoRepository;
    private final GigaChatModelInfoMapper gigaChatModelInfoMapper;

    @Transactional
    protected GigaChatModelInfo saveChatInfo(
            List<Choice> choices,
            CompletionResponse completions,
            String userRequestId,
            String userMessage,
            String status
    ) {
        ChoiceMessage choice = choices.getFirst().message();
        GigaChatModelInfo entity = gigaChatModelInfoMapper.toEntity(
                completions.model(),
                choice.content(),
                choice.role().name(),
                userRequestId,
                userMessage,
                status
        );
        gigaChatModelInfoRepository.save(entity);
        return entity;
    }
}
