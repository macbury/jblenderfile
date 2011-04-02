package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * ListBase wrapper
 * @author pgi
 */
public class BlenderListBase {
    private final List<BlenderFileBlock> blockList;

    public BlenderListBase(SDNAStructure listBaseStructure, BlenderFile file) throws IOException {
        if(listBaseStructure == null) {//fix for 2.37
            blockList = Collections.emptyList();
            return;
        }

        BlenderFileBlock first = listBaseStructure.getPointedBlock("first", file);
        BlenderFileBlock last = listBaseStructure.getPointedBlock("last", file);
        List<BlenderFileBlock> list = new LinkedList<BlenderFileBlock>();
        if(first != null) {
            list.add(first);
            while(first != last) {
                first = first.getNextBlock(file);
                list.add(first);
            }
        }
        blockList = Collections.unmodifiableList(list);
    }

    public List<BlenderFileBlock> getElements() {
        return blockList;
    }
}
