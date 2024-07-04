package com.example.gestion_curriculums0;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OpenAiClient {
    private static final String API_KEY = "sk-proj-KRbg801Of7GpB9ehBnebT3BlbkFJfg4gmtYG4YcmU2gDgWrz";
    private static final String URL = "https://api.openai.com/v1/completions";

    public static void main(String[] args) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "text-davinci-003");
        json.put("prompt", "Resume this CV: Tu texto aquí");
        json.put("max_tokens", 150);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
    }
}
