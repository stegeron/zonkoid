package eu.urbancoders.zonkysniper.questions;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.08.2016
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.Question;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private Context context;

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView date, question, answer, questionBy;

        public QuestionViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            question = (TextView) view.findViewById(R.id.question);
            answer = (TextView) view.findViewById(R.id.answer);
            questionBy = (TextView) view.findViewById(R.id.questionBy);
        }
    }


    public QuestionsAdapter(Context context, List<Question> questionList) {
        this.questionList = questionList;
        this.context = context;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_list_row, parent, false);

        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.date.setText(Constants.DATE_DD_MM_YYYY_HH_MM.format(question.getTimeCreated()));
        holder.question.setText(question.getMessage());
        holder.questionBy.setText(question.getQuestedBy().getNickName());
        if(question.getAnswer() != null) {
            holder.answer.setText(question.getAnswer().getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
