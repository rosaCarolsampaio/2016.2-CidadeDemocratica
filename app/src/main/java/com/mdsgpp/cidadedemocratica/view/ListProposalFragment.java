package com.mdsgpp.cidadedemocratica.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mdsgpp.cidadedemocratica.R;
import com.mdsgpp.cidadedemocratica.controller.ProposalListAdapter;
import com.mdsgpp.cidadedemocratica.controller.ProposalsList;
import com.mdsgpp.cidadedemocratica.controller.TagginsList;
import com.mdsgpp.cidadedemocratica.model.Entity;
import com.mdsgpp.cidadedemocratica.model.Proposal;
import com.mdsgpp.cidadedemocratica.persistence.DataUpdateListener;
import com.mdsgpp.cidadedemocratica.persistence.EntityContainer;
import com.mdsgpp.cidadedemocratica.requester.ProposalRequestResponseHandler;
import com.mdsgpp.cidadedemocratica.requester.RequestResponseHandler;
import com.mdsgpp.cidadedemocratica.requester.RequestUpdateListener;
import com.mdsgpp.cidadedemocratica.requester.Requester;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class ListProposalFragment extends Fragment implements DataUpdateListener {

    private OnFragmentInteractionListener mListener;
    private ListView proposalListView;

    public ArrayList<Proposal> proposals;
    public ArrayList<Proposal> favoriteProposals;
    private View header;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    ProposalListAdapter proposalAdapter;
    EntityContainer<Proposal> proposalsContainer = EntityContainer.getInstance(Proposal.class);

    int preLast = 0;
    public ListProposalFragment() {
        // Required empty public constructor
    }

    public static ListProposalFragment newInstance(ArrayList<Proposal> proposals) {


        ListProposalFragment fragment = new ListProposalFragment();

        fragment.proposals = proposals;

        EntityContainer.getInstance(Proposal.class).setDataUpdateListener(fragment);

        return fragment;
    }

    public static ListProposalFragment newInstance(ArrayList<Proposal> proposals, View header) {


        ListProposalFragment fragment = ListProposalFragment.newInstance(proposals);
        fragment.header = header;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_list, container, false);

        getProposalsFavorite();

        ArrayList<Object> objects = new ArrayList<>();
        objects.add("Favoritos");
        objects.add(this.favoriteProposals);
        objects.add("Todos");
        objects.addAll(this.proposals);

        proposalAdapter = new ProposalListAdapter(getContext().getApplicationContext(), objects);

        proposalAdapter.updateData(objects);
        proposalListView = (ListView) view.findViewById(R.id.proposalsListId);

        proposalAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                proposalListView.smoothScrollToPosition(preLast);
            }
        });

        proposalListView.setAdapter(proposalAdapter);

        if (header!=null){
            proposalListView.addHeaderView(header);
        }

        proposalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Proposal proposalClicked = (Proposal)proposalAdapter.getItem(i);
                Long id = proposalClicked.getId();
                String proposalName = proposalClicked.getTitle();

                Intent intent = new Intent(getActivity().getApplicationContext(),TagginsList.class);
                intent.putExtra("proposalId", id);
                startActivity(intent);

            }
        });

        proposalListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int itemCountTrigger = totalItemCount/2;
                final int lastItem = firstVisibleItem + visibleItemCount;

                Activity ac = getActivity();
                if (ac != null) {
                    if (getActivity().getClass() == ProposalsList.class) {
                        if(lastItem == totalItemCount - (itemCountTrigger > 15 ? 15 : itemCountTrigger)) {
                            if(preLast != lastItem) {
                                preLast = lastItem;
                                ((ProposalsList)getActivity()).pullProposalsData();
                            }

                        }
                    } else {

                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void dataUpdated(Class<? extends Entity> entityType) {
        if (entityType == Proposal.class) {
            Activity ac = getActivity();
            if (ac != null) {
                ac.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add("Favoritos");
                        objects.add(favoriteProposals);
                        objects.add("Todos");
                        objects.addAll(proposalsContainer.getAll());
                        proposalAdapter.updateData(objects);
                    }
                });
            }
        }
    }

    public ListView getProposalListView() {
        return proposalListView;
    }



    private void getProposalsFavorite(){
        Requester requester = new Requester(RequestResponseHandler.favoriteProposalsEndpoint, new ProposalRequestResponseHandler(){
            @Override
            protected void afterSuccess(Object response) {
                favoriteProposals = (ArrayList<Proposal>) response;
                countDownLatch.countDown();
                super.afterSuccess(response);
            }

            @Override
            protected void afterError(String message) {
                super.afterError(message);
            }
        });
        countDownLatch.countDown();
        requester.async(Requester.RequestMethod.GET);
    }
}
