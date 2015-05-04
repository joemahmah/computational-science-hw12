package geneticsHomework;

import java.util.ArrayList;
import java.util.List;

public enum GeneType {

    MALE(true),
    BROWN_COAT(false);

    private boolean isDominant;
    private GeneType[] similarGenes;

    private GeneType(boolean isDominant, GeneType... similarGenes) {
        this.isDominant = isDominant;
        this.similarGenes = similarGenes;
    }

    public boolean isDominant() {
        return isDominant;
    }

    public static boolean isDominant(GeneType gt) {
        return gt.isDominant();
    }

    public GeneType[] getSimilarGenesArray() {
        return similarGenes;
    }

    public List<GeneType> getSimilarGenesList() {
        List l = new ArrayList();

        for (GeneType gt : similarGenes) {
            l.add(gt);
        }

        return l;
    }

    public static GeneType[] getSimilarGenesArray(GeneType gt) {
        return gt.getSimilarGenesArray();
    }

    public static List<GeneType> getSimilarGenesList(GeneType gt) {
        return gt.getSimilarGenesList();
    }
}
