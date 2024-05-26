package com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle;

import com.nure.apz.fatianov.daniil.vehiclestationservice.station.Station;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.StationRepository;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.request.VehicleAddRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.request.VehicleChangeRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.response.VehicleGetAllResponseEntity;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.response.objects.Order;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.locationtech.jts.geom.Polygon;

import javax.swing.text.html.Option;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private static final Double BUFFER_RADIUS = 0.2;

    private static final String orderServiceUrl = "http://localhost:8083";

    private final VehicleRepository vehicleRepository;

    private final StationRepository stationRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    public void addVehicle(VehicleAddRequest requestBody) {
        Optional<Station> optionalStation = stationRepository.findByNumber(requestBody.getStationNumber());

        if(optionalStation.isEmpty()) {
            throw new IllegalStateException("Station with number: "
                    + requestBody.getStationNumber() + "does not exist");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setNumber(requestBody.getNumber());
        vehicle.setLiftingCapacity(requestBody.getLiftingCapacity());
        vehicle.setFlightDistance(requestBody.getFlightDistance());
        vehicle.setStatus(Status.AVAILABLE);
        vehicle.setStation(optionalStation.get());

        vehicleRepository.saveAndFlush(vehicle);


    }

    public List<VehicleGetAllResponseEntity> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleGetAllResponseEntity> vehicleGetAllResponseEntities = new ArrayList<>();
        for(Vehicle vehicle : vehicles) {
            VehicleGetAllResponseEntity vehicleGetAllResponseEntity = new VehicleGetAllResponseEntity();
            vehicleGetAllResponseEntity.setId(vehicle.getId());
            vehicleGetAllResponseEntity.setNumber(vehicle.getNumber());
            vehicleGetAllResponseEntity.setLiftingCapacity(vehicle.getLiftingCapacity());
            vehicleGetAllResponseEntity.setFlightDistance(vehicle.getFlightDistance());
            vehicleGetAllResponseEntity.setStatus(vehicle.getStatus());
            vehicleGetAllResponseEntity.setStationNumber(vehicle.getStation().getNumber());

            HttpEntity<Integer> orderHttpEntity = new HttpEntity<>(vehicle.getId());
            String urlToOrderService = orderServiceUrl.concat("/get-order-for-vehicle");
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<Order> responseEntity = restTemplate.postForEntity(urlToOrderService, orderHttpEntity, Order.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    vehicleGetAllResponseEntity.setOrder(responseEntity.getBody());
                }else{
                    vehicleGetAllResponseEntity.setOrder(null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            vehicleGetAllResponseEntities.add(vehicleGetAllResponseEntity);

        }

        return vehicleGetAllResponseEntities;
    }

    public String getVehicleNumber(Integer id) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with id: " + id + " does not exist");
        }

        return optionalVehicle.get().getNumber();
    }

    public void changeVehicle(VehicleChangeRequest requestBody) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(requestBody.getId());

        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with id: " + requestBody.getId() + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setNumber(requestBody.getNumber());
        vehicle.setLiftingCapacity(requestBody.getLiftingCapacity());
        vehicle.setFlightDistance(requestBody.getFlightDistance());

        vehicleRepository.saveAndFlush(vehicle);
    }

    public void getVehicleReady(String number) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByNumber(number);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with number: " + number + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setStatus(Status.READY);
        vehicleRepository.saveAndFlush(vehicle);
    }

    public void deleteVehicle(String number) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByNumber(number);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with number: " + number + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();

        HttpEntity<Integer> orderHttpEntity = new HttpEntity<>(vehicle.getId());
        String urlToOrderService = orderServiceUrl.concat("/get-order-for-vehicle");
        try{
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<Order> responseEntity = restTemplate.postForEntity(urlToOrderService, orderHttpEntity, Order.class);

            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                Order order = responseEntity.getBody();
                if(Order.orderHasDetails(order)) {
                    throw new IllegalStateException("Vehicle with number: " + number + " has order");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        vehicleRepository.delete(vehicle);
    }

    public List<Coordinate> sendVehicle(String number) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByNumber(number);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with number: " + number + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();
        HttpEntity<Integer> orderHttpEntity = new HttpEntity<>(vehicle.getId());
        String urlToOrderService = orderServiceUrl.concat("/get-order-for-vehicle");
        Order order = new Order();
        try{
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<Order> responseEntity = restTemplate.postForEntity(urlToOrderService, orderHttpEntity, Order.class);

            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                order = responseEntity.getBody();
                if(!Order.orderHasDetails(order)) {
                    throw new IllegalStateException("Cannot send drone: " + number + "because wrong order");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Station deptStation = vehicle.getStation();

        Optional<Station> optionalStation = stationRepository.findByNumber(order.getArrivalStationNumber());
        if(optionalStation.isEmpty()) {
            throw new IllegalStateException("Station with number: " + order.getArrivalStationNumber() + " does not exist");
        }

        Station arvlStation = optionalStation.get();

        Double startLat, startLon, endLat, endLon;
        startLat = deptStation.getLatitude();
        startLon = deptStation.getLongitude();
        endLat = arvlStation.getLatitude();
        endLon = arvlStation.getLongitude();

        // Визначення прямокутної області, що охоплює точки A і B, з буферною зоною
        double minLat = Math.min(startLat, endLat) - BUFFER_RADIUS;
        double maxLat = Math.max(startLat, endLat) + BUFFER_RADIUS;
        double minLon = Math.min(startLon, endLon) - BUFFER_RADIUS;
        double maxLon = Math.max(startLon, endLon) + BUFFER_RADIUS;

        Set<Polygon> buildings = new HashSet<>();
        try {
             buildings = loadBuildingsFromOpenStreetMap(minLat, minLon, maxLat, maxLon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Створення початкової та кінцевої точок
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate start = new Coordinate(startLon, startLat);
        Coordinate end = new Coordinate(endLon, endLat);

        //TODO переробити під запит в IoT
        return findShortestPath(start, end, buildings);
    }

    private Set<Polygon> loadBuildingsFromOpenStreetMap(double minLat, double minLon, double maxLat, double maxLon) throws IOException {
        Set<Polygon> buildings = new HashSet<>();
        GeometryFactory geometryFactory = new GeometryFactory();

        String url = String.format("https://www.openstreetmap.org/api/0.6/map?bbox=%f,%f,%f,%f", minLon, minLat, maxLon, maxLat);

        // Використання RestTemplate для завантаження та обробки даних OSM
        ResponseEntity<String> responseEntity = restTemplateBuilder.build().getForEntity(url, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            File tempFile = File.createTempFile("osm_data", ".xml");
            Files.write(tempFile.toPath(), responseBody.getBytes());
            // Обробка XML відповіді за допомогою Osmosis
            XmlReader xmlReader = new XmlReader(tempFile, false, CompressionMethod.None);
            xmlReader.setSink(new Sink(){
                @Override
                public void process(EntityContainer entityContainer) {
                    if (entityContainer.getEntity().getType() == EntityType.Way) {
                        Way way = (Way) entityContainer.getEntity();
                        boolean isBuilding = way.getTags().stream().anyMatch(tag -> tag.getKey().equals("building"));
                        if (isBuilding) {
                            List<Coordinate> coordinates = new ArrayList<>();
                            for (WayNode wayNode : way.getWayNodes()) {
                                double lat = wayNode.getLatitude();
                                double lon = wayNode.getLongitude();
                                coordinates.add(new Coordinate(lon, lat));
                            }
                            if (!coordinates.isEmpty()) {
                                coordinates.add(coordinates.get(0)); // замикання полігону
                                LinearRing linearRing = geometryFactory.createLinearRing(coordinates.toArray(new Coordinate[0]));
                                Polygon building = geometryFactory.createPolygon(linearRing, null);
                                buildings.add(building);
                            }
                        }
                    }
                }

                @Override
                public void initialize(Map<String, Object> metaData) { }

                @Override
                public void complete() { }

                @Override
                public void close() {

                }
            });

            xmlReader.run();
        } else {
            throw new IOException("Failed to fetch data from OpenStreetMap API");
        }

        return buildings;
    }

    // Інші методи залишаються без змін
    private List<Coordinate> findShortestPath(Coordinate start, Coordinate end, Set<Polygon> buildings) {
        PriorityQueue<Coordinate> openSet = new PriorityQueue<>((a, b) -> {
            double distA = a.distance(end);
            double distB = b.distance(end);
            return Double.compare(distA, distB);
        });
        openSet.offer(start);
        Set<Coordinate> closedSet = new HashSet<>();
        Map<Coordinate, Coordinate> cameFrom = new HashMap<>();

        while (!openSet.isEmpty()) {
            Coordinate current = openSet.poll();
            if (current.equals(end)) {
                return reconstructPath(cameFrom, current);
            }
            closedSet.add(current);
            for (Coordinate neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor) || intersectsBuilding(neighbor, buildings)) {
                    continue;
                }
                double tentativeScore = cameFrom.getOrDefault(current, start).distance(neighbor);
                if (!openSet.contains(neighbor) || tentativeScore < cameFrom.getOrDefault(neighbor, start).distance(neighbor)) {
                    cameFrom.put(neighbor, current);
                    openSet.offer(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean intersectsBuilding(Coordinate coordinate, Set<Polygon> buildings) {
        GeometryFactory geometryFactory = new GeometryFactory();
        org.locationtech.jts.geom.Point point = geometryFactory.createPoint(coordinate);
        for (Polygon building : buildings) {
            if (building.intersects(point)) {
                return true;
            }
        }
        return false;
    }

    private List<Coordinate> getNeighbors(Coordinate coordinate) {
        List<Coordinate> neighbors = new ArrayList<>();
        double x = coordinate.x;
        double y = coordinate.y;
        double distance = 0.00009; // Приблизно 10 метрів

        neighbors.add(new Coordinate(x + distance, y));
        neighbors.add(new Coordinate(x - distance, y));
        neighbors.add(new Coordinate(x, y + distance));
        neighbors.add(new Coordinate(x, y - distance));

        return neighbors;
    }

    private List<Coordinate> reconstructPath(Map<Coordinate, Coordinate> cameFrom, Coordinate current) {
        List<Coordinate> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

}
