package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
    public static final int SHARE_CARDSET= 4;
    private List<String> beforeUpdate;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteButton;
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
            deleteButton = itemView.findViewById(R.id.deleteButton);
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
    public void delete(int position){
        mCards.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onViewAttachedToWindow(CardAdapter.ViewHolder viewHolder) {
        EditText editView = viewHolder.getEditView();
        if(editView != null)
            if(viewHolder.getAdapterPosition() == mCards.size() - 1)
                editView.requestFocus();
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
                            final Intent playIntent = new Intent(mContext, PlayActivity.class);
                            playIntent.putExtra("cardSet", item);
                            mContext.startActivity(playIntent);
                            break;

                        case VIEW_CARDSET:
                            final Intent intent = new Intent(mContext, ManageCardsActivity.class);
                            intent.putExtra("cardSet", item);
                            mContext.startActivity(intent);
                            break;

                        case EDIT_CARD:
                            break;
                        case SHARE_CARDSET:

                            try{
                                String fileName = item+"ToShare.charades";
                                File cardSetFile = new File(mContext.getFilesDir(), fileName);
                                Log.d("connections", cardSetFile.getPath());
                                FileWriter writer = new FileWriter(cardSetFile);
                                int len = mCards.size();
                                writer.append(item);
                                for(int i = 0; i < len; i++){
                                    writer.append(mCards.get(i));
                                }
                                writer.flush();
                                writer.close();
                                final String path = cardSetFile.getAbsolutePath();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setMessage(R.string.email_message).setTitle(R.string.email_title);
                                final Activity act = (Activity) mContext;
                                LayoutInflater inflater = act.getLayoutInflater();
                                final View dialogView = inflater.inflate(R.layout.email_dialog, null);
                                builder.setView(dialogView);
                                builder.setPositiveButton(R.string.share_button,null);



                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();
                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener( new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                        emailIntent.setData(Uri.parse("mailto:"));
                                        emailIntent.setType("text/plain");
                                        EditText email_box = dialogView.findViewById(R.id.email_box);
                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, email_box.getText().toString());
                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Look at my cool card Set!");
                                        emailIntent.putExtra(Intent.EXTRA_TEXT, "My super cool set is attached! You can upload it right to you app!");
                                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                                        try{
                                            act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                            act.finish();
                                        }catch (android.content.ActivityNotFoundException ex){
                                            Toast.makeText(act, "There is no email client installed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
//                                Intent nfcIntent = new Intent(mContext, MakingConnection.class);
//                                nfcIntent.putExtra("fileName", fileName);
//                                mContext.startActivity(nfcIntent);

                            }catch(Exception e){
                                Log.e("Share", e.toString());
                            }

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
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        delete(position);
                    }
                });

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

