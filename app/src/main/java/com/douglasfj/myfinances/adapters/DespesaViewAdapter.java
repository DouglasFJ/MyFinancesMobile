package com.douglasfj.myfinances.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;

import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.constantes.TipoDespesa;
import com.douglasfj.myfinances.models.ItemDepesa;
import com.douglasfj.myfinances.models.entities.ValoresEntity;
import com.douglasfj.myfinances.utils.ColorUtil;
import com.douglasfj.myfinances.utils.DinheiroUtil;

import java.util.List;

public class DespesaViewAdapter extends ArrayAdapter<ValoresEntity> {
    public DespesaViewAdapter(@NonNull Context context, @NonNull List<ValoresEntity> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_despesa, parent, false);
        }

        ValoresEntity itemDepesa = getItem(position);

        if (itemDepesa != null) {
            //busca todas as views
            TextView nomeDespesaView = itemView.findViewById(R.id.layout_despesa_nome_despesa);
            TextView despesaView = itemView.findViewById(R.id.layout_despesa_despesa);
            View wrapperImagem = itemView.findViewById(R.id.layout_despesa_wrapper_imagem);
            ImageView imagemView = itemView.findViewById(R.id.layout_despesa_imagem);

            TipoDespesa tipoDespesa = itemDepesa.getTipoDespesa();


            // Faz as alterações
            String colorName = tipoDespesa.getColorName();

            nomeDespesaView.setText(itemDepesa.nomeValor);
            nomeDespesaView.setTextColor(
                    ContextCompat.getColor(getContext(),ColorUtil.getColorIDByNameAndWeight(colorName, "950"))
            );

            despesaView.setText(DinheiroUtil.floatToStringReais(itemDepesa.valorReais));
            despesaView.setTextColor(
                    ContextCompat.getColor(getContext(), ColorUtil.getColorIDByNameAndWeight(colorName, "700"))
            );

            // set background and color background
            Drawable bg = wrapperImagem.getBackground();
            bg = DrawableCompat.wrap(bg);
            DrawableCompat.setTint(
                    bg,
                    ContextCompat.getColor(getContext(), ColorUtil.getColorIDByNameAndWeight(colorName, "300"))
            );


            //set image and color image
            imagemView.setImageResource(tipoDespesa.getRIDIcon());
            ColorStateList csl = AppCompatResources.getColorStateList(getContext(), ColorUtil.getColorIDByNameAndWeight(colorName, "950"));
            ImageViewCompat.setImageTintList(imagemView, csl);
        }

        return itemView;
    }
}
