package com.example.andrejavbelj.driveit.VoznjeClass;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrejavbelj.driveit.R;

import java.util.ArrayList;

public class VoznjeList extends RecyclerView.Adapter<VoznjeList.VoznjeListVieweHolder> {

    private ArrayList<Voznje> voznje;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDelete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public static class VoznjeListVieweHolder extends RecyclerView.ViewHolder{
        public TextView ime, kolicina, cena;
        public ImageView odstrani;

        public VoznjeListVieweHolder(View itemView, final OnItemClickListener listener){
                super(itemView);
                ime = itemView.findViewById(R.id.ID_txtViewIme);
                kolicina = itemView.findViewById(R.id.ID_txtViewKolcina);
                cena = itemView.findViewById(R.id.ID_txtViewCena);
                odstrani = itemView.findViewById(R.id.odstrani);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                listener.onItemClick(position);
                            }
                        }
                    }
                });

                odstrani.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                listener.onDelete(position);
                            }
                        }
                    }
                });



        }
    }

    public VoznjeList(ArrayList<Voznje> v){
        voznje = v;
    }


    @NonNull
    @Override
    public VoznjeListVieweHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item, viewGroup, false);
        VoznjeListVieweHolder e = new VoznjeListVieweHolder(v, mListener);
        return e;
    }

    @Override
    public void onBindViewHolder(@NonNull VoznjeListVieweHolder voznjeListVieweHolder, int i) {
        Voznje v = voznje.get(i);

        System.out.println(v.toString());

        voznjeListVieweHolder.ime.setText(v.getIme());
        voznjeListVieweHolder.kolicina.setText(String.valueOf(v.getKolicina()));
        voznjeListVieweHolder.cena.setText(String.valueOf(v.getCena()));
    }

    @Override
    public int getItemCount() {
        return voznje.size();
    }
}
