package com.example.learningapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningapp.R;
import com.example.learningapp.backworker.AsyncActivity;
import com.example.learningapp.fragments.MyItemRecyclerViewAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity implements MyCommentItemRecycleViewAdapter.OnCommentInterationListener {

    private RecyclerView mCommentRecyclerView;
    private MyCommentItemRecycleViewAdapter commentAdapter;

    private List<Comment> comments;

    private UpdateCommentAsyncTask updateCommentAsyncTask;

    private int[] imageIds = new int[]{R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);

//        List<Map<String, Object>> items = new ArrayList<>();
//        for (int imageId : imageIds) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("imageId", imageId);
//            items.add(map);
//        }
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, items, R.layout.item_nav_stackview,
//                new String[]{"imageId"}, new int[]{R.id.item_nav_stackview_image});
//        ((StackView) findViewById(R.id.nav_stackview)).setAdapter(simpleAdapter);

        // RecylerView
        mCommentRecyclerView = (RecyclerView)findViewById(R.id.nav_recylerview_comment);

        // 优化
//        mCommentRecyclerView.setHasFixedSize(true);

        // LinearLayoutManager, GridLayoutManager, StaggeredGridLayoutManager（瀑布流，不同高度/宽度的view）
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCommentRecyclerView.setLayoutManager(linearLayoutManager);

        mCommentRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        mCommentRecyclerView.setItemAnimator();

        initData();

        commentAdapter = new MyCommentItemRecycleViewAdapter(comments, this);
        mCommentRecyclerView.setAdapter(commentAdapter);

        mCommentRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });

        // 启动任务通过网络等读取数据，再更新adapter


    }

    private void initData() {
        if(comments == null)
            comments = new ArrayList<>();

        for(int i=0; i<10; ++i){
            int resId = i%2==0? R.drawable.ic_launcher_background : R.drawable.ic_launcher_foreground;
            Comment comment = new Comment(resId, "person"+i, "comment"+i);
            comments.add(comment);
        }
    }

    public void addComments(View view){
        int i = (int)(Math.random()*(comments.size()+1));
        int resId = i%2==0? R.drawable.ic_launcher_background : R.drawable.ic_launcher_foreground;
        Comment newComment = new Comment(resId, "newName"+i, "newComment"+i);
        comments.add(1, newComment);
        commentAdapter.notifyItemInserted(1);
//        commentAdapter.notifyItemRangeChanged(i, commentAdapter.getItemCount());
    }

    public void delComments(View view){
        comments.remove(2);
        commentAdapter.notifyItemRemoved(2);
//        commentAdapter.notifyItemRangeChanged(2, commentAdapter.getItemCount());
    }

    public void updateComments(View view){
        updateCommentAsyncTask = new UpdateCommentAsyncTask(this);
        updateCommentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onCommentClick(View view, Comment comment) {
        Toast.makeText(this, "click comment "+comment.personName, Toast.LENGTH_SHORT).show();
    }

    public void updateComments(List<Comment> comments) {
        int size = comments.size();
        if (size == 0) return;
        comments.addAll(this.comments);
        this.comments = comments;
        commentAdapter.setComments(this.comments);
        // 可放于上面方法中
        commentAdapter.notifyItemRangeChanged(0, size);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateCommentAsyncTask != null){
            updateCommentAsyncTask.cancel(true);
        }
    }
}

class MyCommentItemRecycleViewAdapter extends RecyclerView.Adapter<MyCommentItemRecycleViewAdapter.CommentViewHolder>{

    private List<Comment> comments;
    private OnCommentInterationListener mListener;

    public MyCommentItemRecycleViewAdapter(List<Comment> comments){
         this(comments, null);
    }

    public MyCommentItemRecycleViewAdapter(List<Comment> comments, OnCommentInterationListener listener){
        this.comments = comments;
        mListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_recylerview_comment, parent, false);
        Log.i("RecyclerViewActivity", "recyclerView onCreateViewHolder...");
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, final int position) {
        Log.i("RecyclerViewActivity", "recyclerView onBindViewHolder... position: "+position);
        holder.updateView(comments.get(position));
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(contextWeakReference.get() != null){
//                    String name = "pic";
//                    if(v.getId() == R.id.tv_nav_recylerview_name)
//                        name = "name";
//                    else if(v.getId() == R.id.tv_nav_recylerview_comment)
//                        name = "comment";
//                    Toast.makeText(contextWeakReference.get(), name + " " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
//            }
//        };
        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // position是不变的！！当添加删除时会获取错数据
                    // 改进： 使用holder保存数据，或者调用holder.getLayoutPosition
                    int pos = holder.getLayoutPosition();
                    mListener.onCommentClick(v, comments.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.i("RecyclerViewActivity", "recyclerView getItemCount...");
        return comments.size();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setItemInterationListener(OnCommentInterationListener mListener) {
        this.mListener = mListener;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPic;
        TextView tvName;
        TextView tvComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);    // 保存了itemview！
            ivPic = itemView.findViewById(R.id.iv_nav_recylerview_pic);
            tvName = itemView.findViewById(R.id.tv_nav_recylerview_name);
            tvComment = itemView.findViewById(R.id.tv_nav_recylerview_comment);
        }

        public void updateView(Comment comment){
            ivPic.setImageResource(comment.personPic);
            tvName.setText(comment.personName);
            tvComment.setText(comment.personComment);
        }
    }

    interface OnCommentInterationListener{
        void onCommentClick(View view, Comment comment);
    }

}

class UpdateCommentAsyncTask extends AsyncTask<String, Integer, List<Comment>>{

    private WeakReference<RecyclerViewActivity> activity;

    public UpdateCommentAsyncTask(RecyclerViewActivity activity){
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected List<Comment> doInBackground(String... strings) {
        List<Comment> commentList = new ArrayList<>();
        try {
            Thread.sleep(2000);
            for (int i=0; i<10; ++i){
                commentList.add(new Comment(R.drawable.ic_launcher_foreground, "newName "+i, "updated comment"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    @Override
    protected void onPostExecute(List<Comment> comments) {
        RecyclerViewActivity activity = this.activity.get();
        if (activity != null){
            activity.updateComments(comments);
        }
    }
}

class Comment{
    public int personPic;
    public String personName;
    public String personComment;

    public Comment(int personPic, String personName, String personComment) {
        this.personPic = personPic;
        this.personName = personName;
        this.personComment = personComment;
    }
}