package com.mdsgpp.cidadedemocratica.model;
import android.test.AndroidTestCase;

import com.mdsgpp.cidadedemocratica.persistence.EntityContainer;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Created by guilherme on 15/09/16.
 */
public class UserTest extends AndroidTestCase {

    User user;
    User userIgual;

    User userLarger;
    User userSecondBuider;

    Tag ciclismo;
    Proposal proposalTest;
    Proposal proposalTest2;
    ArrayList<Tag> tags = new ArrayList<Tag>();
    ArrayList<Proposal> proposals  = new ArrayList<Proposal>();

    @Override
    public void setUp() {

        user = newUser();

        ciclismo = newTag();
        tags.add(ciclismo);
        proposalTest = newProposal();
        proposalTest2 = new Proposal(0,"Titulo","content_2",1,1, "", "");
        proposals.add(proposalTest);

        userIgual = new User("Name",14,1,0);
        userLarger = new User("User name",15,2,1);
        user.setMostUsedTags(tags);
    }

    @Test
    public void testBuilder() {
        assertNotNull(user);
    }

    @Test
    public void testGetName() {
        assertTrue(user.getName().equals("Name"));
    }

    @Test
    public void testGetProposalCount() {
        assertEquals(0,user.getProposalCount());
    }

    @Test
    public void testGetsTags() {
        tags.add(ciclismo);
        assertTrue(this.user.getMostUsedTags().equals(tags));
    }

    @Test
    public void testGetProposal(){
        assertTrue(this.user.getProposals().isEmpty());
        user.setProposals(proposals);
        assertEquals(this.user.getProposals(), proposals);
    }

    @Test
    public void testBuider(){
        userSecondBuider = new User("Lucas","descrição",15,24,1);
        assertTrue(userSecondBuider.getName().equals("Lucas"));
        assertTrue(userSecondBuider.getDescription().equals("descrição"));
        assertEquals(15,userSecondBuider.getProposalCount());
        assertEquals(24,userSecondBuider.getId());
        assertEquals(1,userSecondBuider.getRelevance());
    }
    @Test
    public void testToString(){
        assertTrue(user.toString().equals("Name"));
    }

    @Test
    public void testCompareTo(){
        assertEquals(1,user.compareTo(userLarger));
        assertEquals(-1,userLarger.compareTo(user));
        assertEquals(0,user.compareTo(userIgual));
    }

    @Test
    public void testIsEqual() {
        User u1 = newUser();
        User u2 = newUser();

        assertEquals(u1.equals(u2), u1.getId() == u2.getId());

        Object o = new Object();
        assertFalse(u1.equals(o));
    }

    @Override
    protected void tearDown() throws Exception {
        EntityContainer.getInstance(Proposal.class).clear();
    }

    private Tag newTag() {
        return new Tag(0, "name", 0, 0);
    }

    private Proposal newProposal() {
        return new Proposal(0, "title", "content", 0, 0, "", "");
    }
    private User newUser() {return new User("Name", 0, 0,0);}
}