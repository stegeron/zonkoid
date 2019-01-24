package eu.urbancoders.zonkysniper.questions;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.08.2016
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Question;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    ZSViewActivity activity;
    Loan loan;
    private List<Question> questionList;

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView date, question, answer, questionBy;
        public LinearLayout questionRow;
        public LinearLayout editTools;

        public QuestionViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            question = view.findViewById(R.id.question);
            answer = view.findViewById(R.id.answer);
            questionBy = view.findViewById(R.id.questionBy);
            questionRow = view.findViewById(R.id.questionRow);
            editTools = view.findViewById(R.id.editTools);

        }
    }

    public QuestionsAdapter(ZSViewActivity activity, Loan loan, List<Question> questionList) {
        this.questionList = questionList;
        this.activity = activity;
        this.loan = loan;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_list_row, parent, false);

        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.date.setText(Constants.DATE_DD_MM_YYYY_HH_MM.format(question.getTimeCreated()));
        holder.question.setText(question.getMessage());

        String nickName = question.getQuestedBy().getNickName();
        holder.questionBy.setText(nickName);
        // pokud jsem to ja, zvyraznit nickname a umoznit edit klikem / long klikem?
        if(nickName.equalsIgnoreCase(ZonkySniperApplication.getInstance().getUser().getNickName())) {
            holder.editTools.setVisibility(View.VISIBLE);
//            holder.questionBy.setTextColor(ContextCompat.getColor(ZonkySniperApplication.getInstance().getApplicationContext(), R.color.colorAccent));
            holder.questionRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment questionsEditFragment = QuestionsEditFragment.newInstance(questionList.get(holder.getAdapterPosition()), loan);
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_questions, questionsEditFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }


        if(question.getAnswer() != null) {
            holder.answer.setText(question.getAnswer().getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
