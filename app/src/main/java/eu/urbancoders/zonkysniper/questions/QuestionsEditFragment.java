package eu.urbancoders.zonkysniper.questions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Question;
import eu.urbancoders.zonkysniper.events.SendQuestion;
import org.greenrobot.eventbus.EventBus;

public class QuestionsEditFragment extends ZSFragment {

    Loan loan;
    Question question;

    public static QuestionsEditFragment newInstance(Question question, Loan loan) {
        QuestionsEditFragment fragment = new QuestionsEditFragment();
        Bundle args = new Bundle();
        if(question == null) {
            question = new Question();
        }
        args.putSerializable("question", question);
        args.putSerializable("loan", loan);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionsEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_questions_edit, container, false);
        loan = (Loan) getArguments().getSerializable("loan");
        question = (Question) getArguments().getSerializable("question");

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);

        TextView questionTitle = (TextView) rootView.findViewById(R.id.messages_title);

        final EditText questionText = (EditText) rootView.findViewById(R.id.questionText);
        questionText.setHint(loan.getNickName());
        if(question.getMessage() != null && !question.getMessage().isEmpty()) {
            /**
             * editujeme otazku
             */
            questionTitle.setText(R.string.question_edit);
            questionText.setText(question.getMessage());
        }

        Button cancel = (Button)rootView.findViewById(R.id.buttonCancelQuestionEdit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoanDetailsActivity)getActivity()).fab.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });

        Button send = (Button)rootView.findViewById(R.id.buttonSendQuestion);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question.setMessage(questionText.getText().toString());
                EventBus.getDefault().post(new SendQuestion.Request(loan.getId(), question));
                ((LoanDetailsActivity) getActivity()).fab.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });



        return rootView;
    }
}
