package com.cactus.netflixalpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cactus.netflixalpha.model.Category;
import com.cactus.netflixalpha.model.Movie;
import com.cactus.netflixalpha.util.CategoryTask;
import com.cactus.netflixalpha.util.ImageDownloaderTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {

    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycleView = findViewById(R.id.recycle_view_main);

        List<Category>categories = new ArrayList<>();


        mainAdapter = new MainAdapter(categories);
        recycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)); // Gerenciador de layout padrão
        recycleView.setAdapter(mainAdapter);

        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoader(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");

    }

    @Override
    public void onResult(List<Category> categories) {
        mainAdapter.setCategories(categories);
        mainAdapter.notifyDataSetChanged();

    }

    private static class MovieHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    private static class CategoryHolder extends RecyclerView.ViewHolder {
        RecyclerView recycleViewMovie;
        TextView textViewTitle;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            recycleViewMovie = itemView.findViewById(R.id.recycle_view_movie);
        }
    }















    private class MainAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private  List<Category> categories;

        private MainAdapter(List<Category> categories) {
            this.categories= categories;
        }

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            Category category = categories.get(position);
            holder.textViewTitle.setText(category.getName());
            holder.recycleViewMovie.setAdapter(new MovieAdapter(category.getMovies()));
            holder.recycleViewMovie.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

         void setCategories(List<Category> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
        }


    }








    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> implements OnItemClickListener {

        private final List<Movie> movies;

        private MovieAdapter(List<Movie> movies) {
            this.movies= movies;
        }



        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.movie_item, parent, false);
            return new MovieHolder(view,this);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            Movie movie = movies.get(position);
            new ImageDownloaderTask(holder.imageViewCover).execute(movie.getCoverUrl());
//            holder.imageViewCover.setImageResource(movie.getCoverUrl());

        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        @Override
        public void onClick(int posistion) {
            if(movies.get(posistion).getId() <= 3) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                intent.putExtra("id", movies.get(posistion).getId());
                startActivity(intent);
            }
        }
    }

    interface OnItemClickListener {
        void onClick(int posistion);
    }
}
