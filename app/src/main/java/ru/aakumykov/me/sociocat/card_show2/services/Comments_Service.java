package ru.aakumykov.me.sociocat.card_show2.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.models.Comment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Comments_Service {


    public interface iLoadCommentsCallbacks {
        void onCommentsLoadSuccess(List<Comment> list);
        void onCommentsLoadFail(String errorMsg);
    }

    public void loadComments(String start, int count, iLoadCommentsCallbacks callbacks) {
        Observable<List<Comment>> observable = Observable
                .fromCallable(new FetchCommentsCallable(start, count))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<List<Comment>> observer = new Observer<List<Comment>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callbacks.onCommentsLoadFail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Comment> commentsList) {
                callbacks.onCommentsLoadSuccess(commentsList);
            }
        };

        observable.subscribe(observer);
    }

    private List<Comment> fetchCommentsFromServer(String start, int count) {

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Comment> list = new ArrayList<>();

        //for(int i=start; i< (start+count); i++)
            //list.add(new Comment(i));

        return list;
    }

    class FetchCommentsCallable implements Callable<List<Comment>> {

        private String start;
        private int count;

        public FetchCommentsCallable(String start, int count) {
            this.start = start;
            this.count = count;
        }

        @Override
        public List<Comment> call() throws Exception {
            return fetchCommentsFromServer(start, count);
        }
    }



    public interface iPostCommentCallbacks {
        void onPostCommentSuccess(Comment comment);
        void onPostCommentFail(String errorMsg);
    }

    public void postComment(Comment comment, iPostCommentCallbacks callbacks) {
        Observable<Comment> observable = Observable
                .fromCallable(new PostCommentCallable(comment))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<Comment> observer = new Observer<Comment>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callbacks.onPostCommentFail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(Comment comment) {
                callbacks.onPostCommentSuccess(comment);
            }
        };

        observable.subscribe(observer);
    }

    private Comment postCommentToServer(Comment comment) {
        comment.setText(comment.getText()+" @");
        return comment;
    }

    class PostCommentCallable implements Callable<Comment> {

        private Comment comment;

        public PostCommentCallable(Comment comment) {
            this.comment = comment;
        }

        @Override
        public Comment call() throws Exception {
            return postCommentToServer(comment);
        }
    }
}
