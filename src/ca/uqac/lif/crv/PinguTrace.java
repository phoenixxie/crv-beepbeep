package ca.uqac.lif.crv;

import java.util.ArrayList;

public class PinguTrace {
    public enum Status {BLOCKER, BASHER, WALKER, BLOWER, FALLER, BUILDER, DEAD};

    public static Status parseFromString(String in) {
        switch (in) {
            case "BLOCKER":
                return Status.BLOCKER;
            case "BASHER":
                return Status.BASHER;
            case "WALKER":
                return Status.WALKER;
            case "BLOWER":
                return Status.BLOWER;
            case "FALLSER":
                return Status.FALLER;
            case "BUILDER":
                return Status.BUILDER;
            case "DEAD":
                return Status.DEAD;
        }

        return null;
    }

    public static class Vector2D {
        public float x;
        public float y;
    }

    public static class Character {
        public int id;
        public Status status;
        public Vector2D position;
        public Vector2D velocity;
    }

    private long timestamp = 0;

    private ArrayList<Character> characters = new ArrayList<>();

    public PinguTrace() {
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void addCharacter(Character c) {
        characters.add(c);
    }

}
