package com.example.xnewsapiapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText input;
    Button search;

    RecyclerView newsRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    NewsRecyclerViewAdapter mNewsRecyclerViewAdapter;
    String query = "";
    ObjectRequest mObjectRequest = new ObjectRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsRecyclerView = findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        newsRecyclerView.setLayoutManager(mLayoutManager);


        input = findViewById(R.id.editText);
        search = findViewById(R.id.button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().length() > 0) {
                    ArticlesDownloader downloader = new ArticlesDownloader();
                    mObjectRequest = downloader.doInBackground("http://newsapi.org/v2/everything?q=" + input.getText() + "&from=2020-05-15&sortBy=publishedAt&apiKey=a9d22990fcb44ec198cab1c52fc5d180");
                    mNewsRecyclerViewAdapter = new NewsRecyclerViewAdapter(mObjectRequest.articles);

                    newsRecyclerView.setAdapter(mNewsRecyclerViewAdapter);
                }
                else {
                    Toast.makeText(MainActivity.this, "Field is empty !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

class ArticlesDownloader extends AsyncTask<String, Void, ObjectRequest> {

    @Override
    protected ObjectRequest doInBackground(String... strings) {
        String res = "";
        ObjectRequest request = new ObjectRequest();
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            int data = reader.read();

            while (data != -1) {
                char current = (char) data;
                res += current;
                data = reader.read();
            }

            JSONObject jsonObject = new JSONObject(res);
            request.status = jsonObject.getString("status");
            JSONArray jsonArticles = new JSONArray(jsonObject.getJSONArray("articles"));

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject jsonObject1 = new JSONObject(jsonArticles.getJSONObject(i).getString("source"));
                String name = jsonObject1.getString("name");
                String author = jsonArticles.getJSONObject(i).getString("author");
                String title = jsonArticles.getJSONObject(i).getString("title");
                String des = jsonArticles.getJSONObject(i).getString("description");
                String newsURL = jsonArticles.getJSONObject(i).getString("url");
                String imageURL = jsonArticles.getJSONObject(i).getString("urlToImage");
                String publishedAt = jsonArticles.getJSONObject(i).getString("publishedAt");
                request.articles.add(new Article(name, author, title, newsURL, imageURL, publishedAt, des));
            }

            return request;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder> {

    ArrayList<Article> mArticles;
    DownloadPicture mPictureDownloader = new DownloadPicture();

    public NewsRecyclerViewAdapter(ArrayList<Article> articles) {
        mArticles = articles;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item_view, parent, false);
        NewsViewHolder holder = new NewsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, int position) {

        holder.mTitle.setText(mArticles.get(position).title);
        holder.mDescription.setText(mArticles.get(position).content);
        if (mPictureDownloader.doInBackground(mArticles.get(position).Image_url) != null)
            holder.mImageView.setImageBitmap(mPictureDownloader.doInBackground(mArticles.get(position).Image_url));

        String btn_url = mArticles.get(position).url;
        holder.url_btn = btn_url;
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }


    class NewsViewHolder extends RecyclerView.ViewHolder {
        //news Recycler views
        ImageView mImageView;
        TextView mTitle, mDescription;
        Button urlBtn;

        String url_btn = "";


        public void inflateViews() {
            mImageView = itemView.findViewById(R.id.news_list_image_view);
            mTitle = itemView.findViewById(R.id.news_list_title);
            mDescription = itemView.findViewById(R.id.news_list_description);
            urlBtn = itemView.findViewById(R.id.news_list_url_btn);
        }

        public NewsViewHolder(@NonNull final View itemView) {
            super(itemView);
            inflateViews();

            urlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), NewsActivity.class);
                    intent.putExtra("content", url_btn);
                }
            });
        }
    }

}

