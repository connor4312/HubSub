package io.peet.hubsub.protocol;

import java.util.List;

public class Command {

    protected List<Packet> data;

    public Command(List<Packet> data) {
        this.data = data;
    }

    /**
     * Returns the name of this command (will always be lower case)
     * @return the command name
     */
    public String name() {
        return data.get(0).toString().toLowerCase();
    }

    /**
     * Returns the number of arguments the command has.
     * @return the number of command arguments
     */
    public int args() {
        return data.size() - 1;
    }

    /**
     * Returns the arg at the given index.
     * @param index the argument number to get
     * @return the packet at that argument position
     */
    public Packet arg(int index) {
        return data.get(index + 1);
    }
}
