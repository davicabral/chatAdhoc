package com.davioliveira.cantalk.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davioliveira.cantalk.ListaContatosActivity;
import com.davioliveira.cantalk.R;
import com.davioliveira.cantalk.utils.Pessoa;

public class AdapterContatos extends BaseAdapter{

	private ArrayList<Pessoa> listaContatos;
	private Context context;
	
	public AdapterContatos(Context context){
		this.context = context;
		listaContatos = new ArrayList<Pessoa>();
	}

	public void addContact(Pessoa pessoa){
		listaContatos.add(pessoa);
		notifyDataSetChanged();
	}
	
	public void addContacts(List<Pessoa> pessoas){
		for (Pessoa pessoa : pessoas) {
			addContact(pessoa);
		}
	}

	public void remContact(Pessoa pessoa){
		listaContatos.remove(pessoa);
		notifyDataSetChanged();
	}

	public void remContact(int position){
		listaContatos.remove(position);
		notifyDataSetChanged();
	}
		
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listaContatos.size();
	}

	@Override
	public Pessoa getItem(int position) {
		return listaContatos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position,final View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);

		View rowView = inflater.inflate(R.layout.contact_cell_layout, null);

		Pessoa p = getItem(position);
		
		TextView txtNome = (TextView) rowView.findViewById(R.id.nome);
		TextView txtEmail = (TextView) rowView.findViewById(R.id.email);
		TextView txtCel = (TextView) rowView.findViewById(R.id.telefone);
		ImageView photo = (ImageView) rowView.findViewById(R.id.fotoContato);
		
		txtNome.setText(p.getNome());
		txtEmail.setText(p.getEmail());
		txtCel.setText(p.getCelular());
		if(p.getFoto() != null)
		{
			String foto = p.getFoto();
			 byte[] imageAsBytes = Base64.decode(foto.getBytes(), Base64.DEFAULT);
			    
			    photo.setImageBitmap(
			            BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
			    );
		}
		if(ListaContatosActivity.selectedItemId == position)
		{
			rowView.setBackgroundResource(R.color.selectedItem);
		}
		else
		{
			rowView.setBackgroundResource(R.color.backgroundItens);
		}
		return rowView;
	}
	
	public void removeItem(int i) {
		
		listaContatos.remove(i);
		notifyDataSetChanged();
	}

	public void removeAll() {
		listaContatos.clear();
	}

}
