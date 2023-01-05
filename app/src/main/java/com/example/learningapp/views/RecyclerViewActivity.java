package com.example.learningapp.views;

import static com.example.learningapp.views.RecyclerViewActivity.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
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

public class RecyclerViewActivity extends AppCompatActivity {

    public static final String TAG = "RecyclerViewActivity";
    private RecyclerView mCommentRecyclerView;
    private MyCommentItemRecycleViewAdapter commentAdapter;

    private List<Comment> comments;

    private UpdateCommentAsyncTask updateCommentAsyncTask;

    private int[] imageIds = new int[]{R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
    private int totalSize;

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

        commentAdapter = new MyCommentItemRecycleViewAdapter(comments);
        mCommentRecyclerView.setAdapter(commentAdapter);

        mCommentRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });

        // 缓存
        mCommentRecyclerView.setItemViewCacheSize(4);

        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.putRecycledView(commentAdapter.onCreateViewHolder(mCommentRecyclerView, 0));
        recycledViewPool.putRecycledView(commentAdapter.onCreateViewHolder(mCommentRecyclerView, 1));
        mCommentRecyclerView.setRecycledViewPool(recycledViewPool);


        // Payload DiffUtils


        // 启动任务通过网络等读取数据，再更新adapter


    }

    private void initData() {
        if(comments == null)
            comments = new ArrayList<>();

        for(int i=0; i<10; ++i){
            int resId = i%2==0? R.drawable.ic_launcher_background : R.drawable.ic_launcher_foreground;
            Comment comment = new Comment(i, resId, "person"+i, "comment"+i);
            comments.add(comment);
        }
        totalSize = 10;
    }

    public void addComments(View view){
        int i = totalSize++;
        int resId = i%2==0? R.drawable.ic_launcher_background : R.drawable.ic_launcher_foreground;
        Comment newComment = new Comment(i, resId, "newName"+i, "newComment"+i);
        comments.add(1, newComment);
        commentAdapter.notifyItemInserted(comments.size() - 1);
//        commentAdapter.notifyItemRangeChanged(i, commentAdapter.getItemCount());
    }

    public void delComments(View view){
        comments.remove(2);
        commentAdapter.notifyItemRemoved(2);
//        commentAdapter.notifyItemRangeChanged(2, commentAdapter.getItemCount());
    }

    public void updateCommentsByPayloads(View view){
        Comment comment = comments.get(0);
        comment.personComment = "payload comment";
        commentAdapter.notifyItemChanged(0, new CommentPayload(CommentPayload.PAYLOAD_COMMENT));
    }

    public void updateCommentsByDiffUtil(View view) {
        List<Comment> oldComments = comments;
        List<Comment> newComments = new ArrayList<>();
        for(int i=0; i<10; ++i){
            int resId = i%2==0? R.drawable.ic_launcher_background : R.drawable.ic_launcher_foreground;
            Comment comment = new Comment(i, resId, "person"+i, "comment"+i);
            if (i % 3 == 0) {
                comment.personComment = "NewCommentsByDiffUtil";
            }
            newComments.add(comment);
        }

        // 可异步
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldComments.size();
            }

            @Override
            public int getNewListSize() {
                return newComments.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldComments.get(oldItemPosition).commentId == newComments.get(newItemPosition).commentId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Comment oldComment = oldComments.get(oldItemPosition);
                Comment newComment = newComments.get(oldItemPosition);
                Log.d(TAG, "areContentsTheSame oldComment=" + oldComment+ "   newComments=" + newComments);
                return oldComment.personComment.equals(newComment.personComment);
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                Comment oldComment = oldComments.get(oldItemPosition);
                Comment newComment = newComments.get(oldItemPosition);
                Log.d(TAG, "areContentsTheSame oldComment=" + oldComment+ "   newComments=" + newComments);
                return new CommentPayload(CommentPayload.PAYLOAD_COMMENT);
            }
        });

        oldComments.clear();
        oldComments.addAll(newComments);

        // ui线程
        diffResult.dispatchUpdatesTo(commentAdapter);

    }

    public void updateComments(View view){
        updateCommentAsyncTask = new UpdateCommentAsyncTask(this);
        updateCommentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

class CommentPayload {
    public static final int PAYLOAD_COMMENT = 1;

    public int type;

    public CommentPayload(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CommentPayload{" +
                "type=" + type +
                '}';
    }
}

class MyCommentItemRecycleViewAdapter extends RecyclerView.Adapter<MyCommentItemRecycleViewAdapter.CommentViewHolder>{

    private List<Comment> comments;

    public MyCommentItemRecycleViewAdapter(List<Comment> comments){
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_recylerview_comment, parent, false);
        Log.i("RecyclerViewActivity", "recyclerView onCreateViewHolder...");
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        // 局部更新comment
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Log.d(TAG, "onBind by payload " + payloads);
            Comment comment = comments.get(position);
            for (Object obj: payloads) {
                if (obj instanceof CommentPayload) {
                    holder.tvComment.setText(comment.personComment);
                }
            }
        }
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // position是不变的！！当添加删除时会获取错数据
                    // 改进： 使用holder保存数据，或者调用holder.getLayoutPosition
                    int pos = holder.getLayoutPosition();
                    Toast.makeText(v.getContext(), "click comment " + comments.get(pos).personName, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public int getItemCount() {
        Log.i("RecyclerViewActivity", "recyclerView getItemCount...");
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return comments.get(position).commentType;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
                commentList.add(new Comment(i, R.drawable.ic_launcher_foreground, "newName "+i, "updated comment"));
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
    public int commentId;
    public int commentType;
    public int personPic;
    public String personName;
    public String personComment;

    public Comment(int commentId, int personPic, String personName, String personComment) {
        this.commentId = commentId;
        commentType = commentId % 2;
        this.personPic = personPic;
        this.personName = personName;
        this.personComment = personComment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", commentType=" + commentType +
                ", personPic=" + personPic +
                ", personName='" + personName + '\'' +
                ", personComment='" + personComment + '\'' +
                '}';
    }
}