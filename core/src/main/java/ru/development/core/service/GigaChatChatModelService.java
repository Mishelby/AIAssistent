package ru.development.core.config;

import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.langchain4j.GigaChatChatModel;
import chat.giga.langchain4j.GigaChatChatRequestParameters;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;

import org.springframework.stereotype.Service;

@Service
public class GigaChatChatModelService{


    public GigaChatChatModel gigaChatChatModel() {
        return GigaChatChatModel.builder()
                .defaultChatRequestParameters(GigaChatChatRequestParameters.builder()
                        .modelName(ModelName.GIGA_CHAT)
                        .build())
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey("")
                                .build())
                        .build())
                .logRequests(true)
                .logResponses(true)
                .build();
    }


}
