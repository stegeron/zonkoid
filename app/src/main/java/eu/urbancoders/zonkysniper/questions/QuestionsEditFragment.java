package eu.urbancoders.zonkysniper.questions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Question;
import eu.urbancoders.zonkysniper.events.SendQuestion;
import org.greenrobot.eventbus.EventBus;

public class QuestionsEditFragment extends Fragment {

    Loan loan;
    Question question;
    Integer questionId;

    public static QuestionsEditFragment newInstance(Integer questionId, Loan loan) {
        QuestionsEditFragment fragment = new QuestionsEditFragment();
        Bundle args = new Bundle();
        args.putSerializable("questionId", questionId);
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
        questionId = getArguments().getInt("questionId");

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);

        final EditText questionText = (EditText) rootView.findViewById(R.id.questionText);
        questionText.setHint(loan.getNickName());

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
                if(question == null) {
                    question = new Question();
                }
                question.setMessage(questionText.getText().toString());
                EventBus.getDefault().post(new SendQuestion.Request(loan.getId(), question));
                ((LoanDetailsActivity) getActivity()).fab.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });



        return rootView;
    }

    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessagesReceived(GetQuestions.Response evt) {
//        // nothing to do
//    }
}
