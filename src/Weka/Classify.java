package Weka;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.Id3;
import weka.classifiers.trees.J48;

public class Classify {

	//enum
	public enum WekaClassifier {
		NAIVE_BAYES, J48, IBk, ID3
	}

	//Return a specific Classifier such as NAIVE_BAYES, J48, IBk, ID3
	public Classifier getClassifier(WekaClassifier classifier) throws IllegalArgumentException {
		try {
			switch (classifier) {
			case NAIVE_BAYES:
				NaiveBayes bayes = new NaiveBayes();
				bayes.setOptions(weka.core.Utils.splitOptions("-D"));
				return bayes;
			case J48:
				J48 j48 = new J48();
				j48.setOptions(new String[] { "-C", "0.25", "-M", "2" });
				return j48;
			case IBk:
				IBk knn = new IBk(3);
				return knn;
			case ID3:
				return new Id3();
			default:
				throw new IllegalArgumentException("Classifier " + classifier + " not found!");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/*-I
	  Weight neighbours by the inverse of their distance
	  (use when k > 1)
	 -F
	  Weight neighbours by 1 - their distance
	  (use when k > 1)
	 -K <number of neighbors>
	  Number of nearest neighbours (k) used in classification.
	  (Default = 1)
	 -A
	  The nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).
	 */
	
	//Customize IBk
	public void setKNN(IBk knn, int k, String IorF, boolean crossValidate, String neibourSearch) throws Exception {
		if (k > 0) {
			knn.setKNN(k);
		}
		if (IorF != null) {
			if (IorF.equals("I")) {
				knn.setOptions(new String[] { "-I" });
			}
			if (IorF.equals("F")) {
				knn.setOptions(new String[] { "-F" });
			}
		}
		knn.setCrossValidate(crossValidate);

		if (neibourSearch != null) {
			if (neibourSearch.equals("BallTrue")) {
				knn.setNearestNeighbourSearchAlgorithm(new weka.core.neighboursearch.BallTree());
			}
			if (neibourSearch.equals("CoverTree")) {
				knn.setNearestNeighbourSearchAlgorithm(new weka.core.neighboursearch.CoverTree());
			}
			if (neibourSearch.equals("FilteredNeighbourSearch")) {
				knn.setNearestNeighbourSearchAlgorithm(new weka.core.neighboursearch.FilteredNeighbourSearch());
			}
			if (neibourSearch.equals("KDTree")) {
				knn.setNearestNeighbourSearchAlgorithm(new weka.core.neighboursearch.KDTree());
			}
			if (neibourSearch.equals("LinearNNSearch")) {
				knn.setNearestNeighbourSearchAlgorithm(new weka.core.neighboursearch.LinearNNSearch());
			}
		}
	}

	/*-K:  Use kernel density estimator rather than normal  distribution for numeric attributes
	-D:  Use supervised discretization to process numeric attributes
	-O:  Display model in old format (good when there are many classes)*/
	
	//Customize Bayes
	public void setBayes(NaiveBayes bayes, boolean k, boolean d, boolean o) {
		bayes.setDisplayModelInOldFormat(o);
		bayes.setUseSupervisedDiscretization(d);
		bayes.setUseKernelEstimator(k);
	}

	/*
	 * -U Use unpruned tree. -C <pruning confidence> Set confidence threshold for *
	 * pruning. (default 0.25) -M <minimum number of instances> Set minimum number
	 * of instances per leaf. (default 2) -N <number of folds> Set number of folds
	 * for reduced error pruning. One fold is used as pruning set. (default 3) -B
	 * Use binary splits only. -L Do not clean up after the tree has been built. -A
	 * Laplace smoothing for predicted probabilities. -Q <seed> Seed for random data
	 * shuffling (default 1).
	 */
	
	//Customize J48
	public void setJ48(J48 j48, boolean u, double confidence, int minimunInstances, int folds, boolean binarySplit,
			int seedShuffling) {
		j48.setUnpruned(u);
		j48.setBinarySplits(binarySplit);

		if (confidence > 0)
			j48.setConfidenceFactor((float) confidence);

		if (minimunInstances > 0)
			j48.setMinNumObj(minimunInstances);

		if (folds > 0)
			j48.setNumFolds(folds);

		if (seedShuffling > 0)
			j48.setSeed(seedShuffling);
	}

}
