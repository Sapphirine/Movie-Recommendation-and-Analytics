package org.apache.mahout.mahout_core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class Simple_evaluation 
{	
    public static void main( String[] args ) throws IOException, TasteException
    {	long start = System.nanoTime();
    	//DataModel model = new FileDataModel(new File("/Users/justin/OneDrive/COURSES 2015 Fall/BIG DATA ANALYTICS/final/yahoo_movie/ratingall.txt"));
    	DataModel model = new FileDataModel(new File("/Users/justin/OneDrive/COURSES 2015 Fall/BIG DATA ANALYTICS/final/yahoo_movie/ratinggroup"+8+".txt"));
    	RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator ();
    	RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
    		public Recommender buildRecommender(
    		DataModel model) throws TasteException {
    		//ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);	
    		//UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
    		EuclideanDistanceSimilarity similarity = new EuclideanDistanceSimilarity(model);
    		//UserSimilarity similarity = new CachingUserSimilarity(new SpearmanCorrelationSimilarity(model), model);
    		//LogLikelihoodSimilarity similarity = new LogLikelihoodSimilarity(model);
    		//UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.5, similarity, model);
    		UserNeighborhood neighborhood = new NearestNUserNeighborhood(4, similarity, model);
    		return new GenericUserBasedRecommender(model, neighborhood, similarity);
    		//return new GenericItemBasedRecommender(model, similarity);
    		}
    	};
    	double score=0; int N=100;
    	for (int i=0; i<N; i++) {
    		score += evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05);
    		}
    	System.out.println("Score = "+score/N);
    	long end = System.nanoTime();
    	System.out.println("Running time = "+(double)(end-start)/1000000000);		
    }
    
}