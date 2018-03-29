package eu.urbancoders.zonkysniper.loandetail;

import android.os.Bundle;

import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.Loan;

/**
 * Prehled splatek do jedne pujcky, mozna i graf splaceni
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 09.03.2018
 */

public class RepaymentsFragment extends ZSFragment {

    private static RepaymentsFragment fragment;

    public RepaymentsFragment() {
    }

    public static RepaymentsFragment newInstance(Loan loan) {
        fragment = new RepaymentsFragment();
        Bundle args = new Bundle();
        args.putSerializable("loan", loan);
        fragment.setArguments(args);
        return fragment;
    }




}
