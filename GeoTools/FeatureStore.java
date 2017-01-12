package org.geotools.data;
/*
可读写
继承自FeatureSource接口，添加增删改要素的方法,


 DataStore myDataStore = ...
 FeatureSource featureSource = myDataStore.getFeatureSource("aname");
 if (featureSource instanceof FeatureStore) {
     // we have write access to the feature data
     FeatureStore featureStore = (FeatureStore) featureSource;

     // add some new features
     Transaction t = new DefaultTransaction("add");
     featureStore.setTransaction(t);
     try {
         featureStore.addFeatures( someFeatures );
         t.commit();
     } catch (Exception ex) {
         ex.printStackTrace();
         t.rollback();
     } finally {
         t.close();
     }
 }

*/
interface FeatureStore<T extends FeatureType, F extends Feature> extends FeatureSource<T, F> {
	//