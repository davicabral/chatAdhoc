package com.davioliveira.cantalk.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.davioliveira.cantalk.R;
import com.davioliveira.cantalk.utils.Mensagem;

public class AdapterChat extends BaseAdapter {

	ArrayList<Mensagem> mensagens;
	Context context;
	
	public AdapterChat(Context context, ArrayList<Mensagem> mensagens) {
		// TODO Auto-generated constructor stub
		this.mensagens = mensagens;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mensagens.size();
	}

	@Override
	public Mensagem getItem(int position) {
		// TODO Auto-generated method stub
		return mensagens.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View listRow = LayoutInflater.from(context).inflate(R.layout.chat_layout, null);
		
		Mensagem m = mensagens.get(position);
		
		TextView txtRemetente1 = (TextView) listRow.findViewById(R.id.rem1);
		TextView txtMensagem1 = (TextView) listRow.findViewById(R.id.men1);
		TextView txtRemetente2 = (TextView) listRow.findViewById(R.id.rem2);
		TextView txtMensagem2 = (TextView) listRow.findViewById(R.id.men2);
		
		txtRemetente2.setText(m.getRemContato());
		txtMensagem2.setText(m.getMsgOk());
		txtRemetente1.setText(m.getRemVc());
		txtMensagem1.setText(m.getMsgEnviada());
		
		return listRow;
	}
	
	public void removeView(int position)
	{
		mensagens.remove(position);
		notifyDataSetChanged();
	}
	
	public void addView(Mensagem mensagem)
	{
		mensagens.add(mensagem);
		notifyDataSetChanged();
	}

}
