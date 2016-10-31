package com.theghouls.pictionis.Model;

import android.widget.ImageView;

/**
 * Created by admin on 31/10/2016.
 */

public class Player {

    private String name;
    private ImageView avatar = null;
    private boolean isAdmin;
    private int team;
    private int wonGame;
    private int looseGame;

    public Player(String name, int team, boolean isAdmin) {

        this.name = name;
        this.isAdmin = isAdmin;
        this.team = team;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getWonGame() {
        return wonGame;
    }

    public void setWonGame(int wonGame) {
        this.wonGame = wonGame;
    }

    public int getLooseGame() {
        return looseGame;
    }

    public void setLooseGame(int looseGame) {
        this.looseGame = looseGame;
    }
}
