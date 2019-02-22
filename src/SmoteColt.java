import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.linalg.Algebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SmoteColt {
    public static void main(String args[]) {
        ArrayList<double[]> data = new ArrayList<>();
        data.add(new double[]{0, 1, 0, 1});
        data.add(new double[]{0, 2, 0, 1});
        data.add(new double[]{3, 1, 0, 1});
        data.add(new double[]{4, 1, 0, 0});
        data.add(new double[]{6, 1, 0, 0});

        data.add(new double[]{0, 1, 0, 1});
        data.add(new double[]{0, 3, 0, 1});
        data.add(new double[]{7, 1, 0, 1});
        data.add(new double[]{4, 4, 0, 0});
        data.add(new double[]{6, 3, 0, 0});

        SmoteColt smoteColt = new SmoteColt(data);
        smoteColt.setNearestNeibors(3);
        ArrayList<double[]> smoteData = smoteColt.getSmoteData();
        for (double[] doubles : smoteData) {
            for (double d : doubles)
                System.out.print(d + "\t");
            System.out.println();
        }
    }

    ArrayList<double[]> rawData;
    double percentage;
    int nearNei;

    public SmoteColt(ArrayList<double[]> rawData) {
        this.rawData = rawData;
        percentage = 100;
        nearNei = 5;
    }

    public void setPercentage(double d) {
        this.percentage = d;
    }

    public void setNearestNeibors(int k) {
        this.nearNei = k;
    }

    public ArrayList<double[]> getSmoteData() {
        int po = 0, ne = 0;
        for (double[] doubles : rawData) {
            if (doubles[doubles.length - 1] == 1)
                po++;
            else
                ne++;
        }

        int classValue = po >= ne ? 0 : 1;
        ArrayList<double[]> needSmote = new ArrayList<>();
        ArrayList<double[]> otherData = new ArrayList<>();
        for (double[] doubles : rawData) {
            if (doubles[doubles.length - 1] == classValue)
                needSmote.add(doubles);
            else
                otherData.add(doubles);
        }

        doSmote(needSmote, classValue);
        otherData.addAll(needSmote);
        Collections.shuffle(otherData);

        return otherData;
    }

    private void doSmote(ArrayList<double[]> data, int classValue) {
        int addDataNum = (int) (percentage * data.size()) / 100;
        ArrayList<double[]> addData = new ArrayList<>();

        for (int i = 0; i < addDataNum; i++) {
            double[] oneData = data.get(i);
            double[][] distance = new double[nearNei][];
            for (int j = 0; j < data.size(); j++) {
                if (i == j)
                    continue;

                double[] delete = new double[oneData.length];
                double[] other = data.get(j);
                for (int k = 0; k < oneData.length; k++)
                    delete[k] = other[k] - oneData[k];

                DenseDoubleMatrix1D denseDoubleMatrix1D = new DenseDoubleMatrix1D(delete);
                double euDistan = Algebra.DEFAULT.norm2(denseDoubleMatrix1D);
                for (int k = 0; k < nearNei; k++)
                    if (distance[k] == null || distance[k][0] > euDistan) {
                        distance[k] = new double[]{euDistan, j};
                        break;
                    }
                delete = null;
            }

            int nn = (int) (Math.random() * nearNei);
            double[] sample = data.get((int) distance[nn][1]);
            double[] doubles = new double[oneData.length];
            for (int j = 0; j < oneData.length - 1; j++)
                doubles[j] = sample[j] + (sample[j] - oneData[j]) * Math.random();
            doubles[oneData.length - 1] = classValue;
            addData.add(doubles);

            distance = null;
        }
        data.addAll(addData);
    }
}
