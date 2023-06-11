package com.gachon.indooroftower;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class KNNLocalization {
//
//    //기존 데이터베이스에 있는 AP값들 저장..인데 뭔가 이상..
//    List<APSignal> apSignals = new ArrayList<>();
//
//    // 현재 위치 추정을 위한 KNN 알고리즘
//    public Location estimateCurrentLocation(List<APSignal> inputAPSignals, int k) {
//        apSignals.clear();
//        apSignals.addAll(inputAPSignals);
//
//        List<Distance> distances = new ArrayList<>();
//
//        // 모든 AP 신호와 입력된 AP 신호의 거리 계산
//        for (APSignal apSignal : apSignals) {
//            for (APSignal inputSignal : inputAPSignals) {
//                double distance = calculateDistance(apSignal, inputSignal);
//                distances.add(new Distance(apSignal, distance));
//            }
//        }
//
//        // 거리를 기준으로 정렬
//        Collections.sort(distances, new Comparator<Distance>() {
//            @Override
//            public int compare(Distance d1, Distance d2) {
//                return Double.compare(d1.distance, d2.distance);
//            }
//        });
//
//        // K개의 최근접 이웃 선택
//        List<APSignal> nearestNeighbors = new ArrayList<>();
//        for (int i = 0; i < k; i++) {
//            nearestNeighbors.add(distances.get(i).apSignal);
//        }
//
//        // 가장 많은 이웃의 위치를 현재 위치로 추정
//        return getMostFrequentLocation(nearestNeighbors);
//    }
//
//    // 두 AP 신호의 거리 계산
//    private double calculateDistance(APSignal apSignal1, APSignal apSignal2) {
//        // 거리 계산 알고리즘 구현 (예: RSSI 값의 차이 계산 등)
//
//        // 예시: RSSI 값을 사용하는 경우
//        double rssi1 = apSignal1.getRSSI();
//        double rssi2 = apSignal2.getRSSI();
//
//        return Math.abs(rssi1 - rssi2);
//    }
//
//    // 가장 많은 이웃의 위치 반환
//    private Location getMostFrequentLocation(List<APSignal> nearestNeighbors) {
//        // 위치 기반으로 가장 많은 이웃 찾기
//
//        // 예시: 모든 이웃의 위치를 리스트에 추가하고, 가장 많이 등장하는 위치 반환
//        List<Location> locations = new ArrayList<>();
//        for (APSignal apSignal : nearestNeighbors) {
//            locations.add(apSignal.getLocation());
//        }
//
//        // 가장 많이 등장하는 위치 찾기
//        Location mostFrequentLocation = null;
//        int maxCount = 0;
//        for (Location location : locations) {
//            int count = Collections.frequency(locations, location);
//            if (count > maxCount) {
//                maxCount = count;
//                mostFrequentLocation = location;
//            }
//        }
//
//        return mostFrequentLocation;
//    }
//
//    // AP 신호 클래스
//    //AP의 신호(RSSI)와 위치 정보를 저장하는 클래스
//    private static class APSignal {
//        private double rssi;
//        private Location location;
//
//        public APSignal(double rssi, Location location) {
//            this.rssi = rssi;
//            this.location = location;
//        }
//
//        public double getRSSI() {
//            return rssi;
//        }
//
//        public Location getLocation() {
//            return location;
//        }
//    }
//
//    // 거리 클래스
//    //APSignal과 추정 위치 사이의 거리를 저장하는 클래스
//    private static class Distance {
//        private APSignal apSignal;
//        private double distance;
//
//        public Distance(APSignal apSignal, double distance) {
//            this.apSignal = apSignal;
//            this.distance = distance;
//        }
//    }
//
//    // 위치 클래스
//    private static class Location {
//        private double x;
//        private double y;
//
//        public Location(double x, double y) {
//            this.x = x;
//            this.y = y;
//        }
//
//        // 필요한 기타 메서드 구현
//    }
//}


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.LinearNNSearch;
import weka.classifiers.lazy.IBk;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ReadAPData {

    public static void main(String[] args) {
        String csvFilePath = "/path/to/AP_c.csv";  // Replace with your CSV file path

        try (FileReader fileReader = new FileReader(csvFilePath);
             CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader)) {

            List<String> AP_bssid = new ArrayList<>();
            List<Double> AP_location_X = new ArrayList<>();
            List<Double> AP_location_Y = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {
                String bssid = csvRecord.get("bssid");
                double locationX = Double.parseDouble(csvRecord.get("X"));
                double locationY = Double.parseDouble(csvRecord.get("y"));

                AP_bssid.add(bssid);
                AP_location_X.add(locationX);
                AP_location_Y.add(locationY);
            }

            // Print the extracted AP data
            System.out.println("AP BSSIDs: " + AP_bssid);
            System.out.println("AP X Coordinates: " + AP_location_X);
            System.out.println("AP Y Coordinates: " + AP_location_Y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class KNNLocationFinder {

    public static void main(String[] args) throws Exception {
        // Reference point data
        Map<String, Map<String, Double>> referencePoints = new HashMap<>();
        referencePoints.put("RP1", createAPData(1, 2, -60, -70, -80));
        referencePoints.put("RP2", createAPData(3, 4, -65, -75, -85));
        referencePoints.put("RP3", createAPData(5, 6, -50, -60, -70));
        // Add more reference points as needed

        // AP data for the location to be found
        Map<String, Double> locationData = createAPData(0, 0, -62, -73, -82);

        // Prepare the training data
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("AP1"));
        attributes.add(new Attribute("AP2"));
        attributes.add(new Attribute("AP3"));

        List<String> classes = new ArrayList<>();
        classes.add("x");
        classes.add("y");
        Attribute classAttribute = new Attribute("class", classes);
        attributes.add(classAttribute);

        Instances dataset = new Instances("LocationData", attributes, referencePoints.size());
        dataset.setClass(classAttribute);

        for (Map.Entry<String, Map<String, Double>> rp : referencePoints.entrySet()) {
            Map<String, Double> apData = rp.getValue();
            double[] values = new double[dataset.numAttributes()];

            for (int i = 0; i < attributes.size() - 1; i++) {
                values[i] = apData.get(attributes.get(i).name());
            }

            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(dataset);
            dataset.add(instance);
        }

        // Perform KNN regression
        IBk knn = new IBk();
        knn.setKNN(3);  // Number of nearest neighbors to consider
        knn.buildClassifier(dataset);

        // Predict the location using the KNN model
        double[] testValues = new double[dataset.numAttributes() - 1];
        for (int i = 0; i < attributes.size() - 1; i++) {
            testValues[i] = locationData.get(attributes.get(i).name());
        }

        Instance testInstance = new DenseInstance(1.0, testValues);
        testInstance.setDataset(dataset);

        double[] predictedLocation = knn.distributionForInstance(testInstance);

        System.out.println("Predicted Location: (" + predictedLocation[0] + ", " + predictedLocation[1] + ")");
    }

    private static Map<String, Double> createAPData(double x, double y, double ap1, double ap2, double ap3) {
        Map<String, Double> apData = new HashMap<>();
        apData.put("AP1", ap1);
        apData.put("AP2", ap2);
        apData.put("AP3", ap3);
        retrun apData;
    }}}
