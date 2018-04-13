package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.card_name);
        }
        public TextView getNameView() {
            return this.nameTextView;
        }
    }

    public CardAdapter(List<String> Cards, Context context, int onClick) {
        mCards = Cards;
        mContext = context;
        onClickType = onClick;
        this.notifyDataSetChanged();
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View manageView = inflater.inflate(R.layout.item_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(manageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {
        final String item = mCards.get(position);
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
    }

    @Override
    public int getItemCount() {
        if(mCards == null)
            return 0;
        return mCards.size();
    }
}

