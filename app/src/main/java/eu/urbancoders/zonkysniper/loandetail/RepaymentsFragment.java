package eu.urbancoders.zonkysniper.loandetail;

import android.os.Bundle;

import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.Loan;

/**
 * Prehled splatek do jedne pujcky, mozna i graf splaceni
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 09.03.2018
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
