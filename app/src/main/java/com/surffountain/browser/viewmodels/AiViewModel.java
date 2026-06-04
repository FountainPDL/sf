package com.surffountain.browser.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.models.ChatMessage;
import com.surffountain.browser.settings.SettingsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private String pageContent;
    private String pageUrl;
    private String systemPrompt;
    private final SettingsManager settings;

    public AiViewModel() {
        settings = SurfFountainApp.getInstance().getSettingsManager();
    }

    public void setPageContext(String content, String url) {
        this.pageContent = content;
        this.pageUrl = url;
        this.systemPrompt = "You are Surf AI, an intelligent browser assistant. " +
                "The user is browsing: " + url + "\n\n" +
                "Page content:\n" + (content != null ? content.substring(0, Math.min(content.length(), 3000)) : "") +
                "\n\nHelp the user understand, summarize, or interact with this content.";
    }

    public void sendMessage(String userMessage) {
        List<ChatMessage> current = new ArrayList<>(messages.getValue() != null ? messages.getValue() : new ArrayList<>());
        current.add(new ChatMessage(userMessage, ChatMessage.Role.USER));
        messages.postValue(new ArrayList<>(current));
        loading.postValue(true);

        executor.execute(() -> {
            try {
                String response = callAiProvider(userMessage, current);
                List<ChatMessage> updated = new ArrayList<>(messages.getValue() != null ? messages.getValue() : new ArrayList<>());
                updated.add(new ChatMessage(response, ChatMessage.Role.ASSISTANT));
                messages.postValue(updated);
            } catch (Exception e) {
                error.postValue("AI error: " + e.getMessage());
            } finally {
                loading.postValue(false);
            }
        });
    }

    private String callAiProvider(String userMessage, List<ChatMessage> history) throws Exception {
        String provider = settings.getAiProvider();
        String apiKey = settings.getAiApiKey();
        String model = settings.getAiModel();

        if (apiKey.isEmpty()) {
            return "Please configure your AI API key in Settings > AI to use Surf AI.";
        }

        switch (provider) {
            case "openai": return callOpenAI(userMessage, history, apiKey, model);
            case "gemini": return callGemini(userMessage, history, apiKey, model);
            default: return callOpenAI(userMessage, history, apiKey, "gpt-4o-mini");
        }
    }

    private String callOpenAI(String userMessage, List<ChatMessage> history, String apiKey, String model) throws Exception {
        JSONArray messagesJson = new JSONArray();
        if (systemPrompt != null) {
            JSONObject sys = new JSONObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            messagesJson.put(sys);
        }
        for (ChatMessage msg : history) {
            JSONObject m = new JSONObject();
            m.put("role", msg.getRole() == ChatMessage.Role.USER ? "user" : "assistant");
            m.put("content", msg.getContent());
            messagesJson.put(m);
        }
        JSONObject body = new JSONObject();
        body.put("model", model != null ? model : "gpt-4o-mini");
        body.put("messages", messagesJson);
        body.put("max_tokens", 1024);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("OpenAI error: " + response.code());
            JSONObject json = new JSONObject(response.body().string());
            return json.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content");
        }
    }

    private String callGemini(String userMessage, List<ChatMessage> history, String apiKey, String model) throws Exception {
        String geminiModel = (model != null && !model.isEmpty()) ? model : "gemini-pro";
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel +
                ":generateContent?key=" + apiKey;

        JSONArray parts = new JSONArray();
        if (systemPrompt != null) {
            JSONObject sysPart = new JSONObject();
            sysPart.put("text", systemPrompt + "\n\nUser: " + userMessage);
            parts.put(sysPart);
        } else {
            JSONObject part = new JSONObject();
            part.put("text", userMessage);
            parts.put(part);
        }
        JSONObject content = new JSONObject();
        content.put("parts", parts);
        JSONObject body = new JSONObject();
        body.put("contents", new JSONArray().put(content));

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Gemini error: " + response.code());
            JSONObject json = new JSONObject(response.body().string());
            return json.getJSONArray("candidates").getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts").getJSONObject(0)
                    .getString("text");
        }
    }

    public LiveData<List<ChatMessage>> getMessages() { return messages; }
    public LiveData<Boolean> isLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
