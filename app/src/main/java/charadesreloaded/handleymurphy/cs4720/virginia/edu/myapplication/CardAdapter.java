package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huntermurphy on 4/12/18.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<String> mCards;
    private Context mContext;
    private int onClickType; //Behavior of onclick, will be one of the types:
    public static final int PLAY_GAME = 1;
    public static final int VIEW_CARDSET = 2;
    public static final int EDIT_CARD = 3;
    private List<String> beforeUpdate;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public EditText nameEditView;
        public EditTextChangeListener myListener;


        public ViewHolder(View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.card_name);
        }
        public ViewHolder(View itemView, EditTextChangeListener myListener) {
            super(itemView);
            this.nameEditView = itemView.findViewById(R.id.edit_card_name);
            this.myListener = myListener;
            this.nameEditView.addTextChangedListener(this.myListener);
        }
        public EditText getEditView() {return this.nameEditView;}
        public TextView getNameView() {
            return this.nameTextView;
        }
    }

    public CardAdapter(List<String> Cards, Context context, int onClick) {
        beforeUpdate = Cards;
        mCards = Cards;
        mContext = context;
        onClickType = onClick;
        this.notifyDataSetChanged();
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View manageView;
        ViewHolder viewHolder;
        // Inflate the custom layout
        if (onClickType != EDIT_CARD) {
            manageView = inflater.inflate(R.layout.item_card, parent, false);
            viewHolder = new ViewHolder(manageView);
        }else{
            manageView = inflater.inflate(R.layout.edit_item_card, parent, false);
            viewHolder = new ViewHolder(manageView, new EditTextChangeListener());
        }

        // Return a new holder instance
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {
        final String item = mCards.get(position);
        if(this.onClickType != EDIT_CARD){
            TextView textView = viewHolder.nameTextView;
            textView.setText(item);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (onClickType) {

                        case PLAY_GAME:
                            break;

                        case VIEW_CARDSET:
                            final Intent intent = new Intent(mContext, ManageCardsActivity.class);
                            intent.putExtra("cardSet", item);
                            mContext.startActivity(intent);
                            break;

                        case EDIT_CARD:
                            break;

                        default:
                            break; //We shouldn't get here ever, but if we do add some code to basically crash I guess
                    }
                }
            });
        }else{
            try {
                viewHolder.myListener.updatePosition(viewHolder.getAdapterPosition());
                EditText editText = viewHolder.nameEditView;
                //editText.setText(item);
                editText.setText(this.beforeUpdate.get(viewHolder.getAdapterPosition()));
                editText.setHint(R.string.addCardText);
            }catch (NullPointerException e){
                viewHolder.myListener.updatePosition(viewHolder.getAdapterPosition());
                EditText editText = viewHolder.nameEditView;
                editText.setText(item);
                editText.setHint(R.string.addCardText);
            }
        }

    }

    @Override
    public int getItemCount() {
        if(mCards == null)
            return 0;
        return mCards.size();
    }
    private class EditTextChangeListener implements TextWatcher {
        private int position;
        public void updatePosition(int position){
            this.position = position;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i1, int i2, int i3){
            //no ops
        }
        @Override
        public void onTextChanged(CharSequence chars, int i1, int i2, int i3) {
            try {
                String str = chars.toString();
                beforeUpdate.set(position, str);
            }catch (NullPointerException e){
                Log.e("TextChange", "Did it again");
            }

        }
        @Override
        public void afterTextChanged(Editable editable) {
            //no ops
        }
    }
}

