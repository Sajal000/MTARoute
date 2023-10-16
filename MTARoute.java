import java.util.*;
import java.io.*;

public class MTARoute {
	private Map<String, List<Edge>> adjacencyList;

	private static class Edge {
		String toStationName;
		String toStationName2;
		String subwayLine;

		public Edge(String toStationName, String toStationName2, String subwayLine) {
			this.toStationName = toStationName;
			this.toStationName2 = toStationName2;
			this.subwayLine = subwayLine;
		}
	}

	public void SubwaySystem(String filename) throws IOException {
		adjacencyList = new HashMap<>();

		BufferedReader br = new BufferedReader(
				new FileReader("D:\\MTA\\MTA\\mta_stations.csv"));
		br.readLine();
		String line = null;
		HashMap<String, HashSet<String>> subwayConnections = new HashMap<>();
		while ((line = br.readLine()) != null) {
			if (line.length() == 0) {
				continue;
			}

			String[] fields = line.split(",");
			String stationId = fields[1];
			String stationName = fields[2];
			String longLat = fields[3];
			String[] subwayLines = fields[4].split("-");
			HashSet<String> lines = new HashSet<>();
			for (String random : subwayLines) {
				lines.add(random);
			}
			if (subwayConnections.containsKey(stationName)) {
				for (String l : lines) {
					subwayConnections.get(stationName).add(l);

				}
			} else {
				subwayConnections.put(stationName, lines);

			}

		}
		for (String station1 : subwayConnections.keySet()) {
			HashSet<String> strings = subwayConnections.get(station1);

			for (String station2 : subwayConnections.keySet()) {
				HashSet<String> strings1 = subwayConnections.get(station2);

				for (String line1 : strings) {

					if (strings1.contains(line1) && !station1.equals(station2)) {

						addStation(station1, station2, line1);

					}

				}
			}
		}

		br.close();
	}

	private void addStation(String stationName, String stationName2, String subwayLine) {
		List<Edge> edges = adjacencyList.getOrDefault(stationName, new ArrayList<>());

		edges.add(new Edge(stationName, stationName2, subwayLine));
		adjacencyList.put(stationName, edges);
	}

	public List<String> bfs(String startStation, String endStation) {
		Map<String, String> prev = new HashMap<>();
		Map<String, String> subwayLineAtPrevStation = new HashMap<>();
		Set<String> visited = new HashSet<>();

		Queue<String> queue = new LinkedList<>();
		queue.add(startStation);

		ArrayList<String> line = new ArrayList<>();

		while (!queue.isEmpty()) {

			String station = queue.poll();

			if (station.contains(endStation)) {

				break;
			}
			visited.add(station);

			for (Edge edge : adjacencyList.get(station)) {
				String toStation = edge.toStationName2;

				String subwayLine = edge.subwayLine;

				if (!visited.contains(toStation)) {

					prev.put(toStation, station);

					subwayLineAtPrevStation.put(toStation, subwayLine);

					queue.add(toStation);
				}
			}
		}

		List<String> path = new ArrayList<>();
		String station = endStation;

		while (prev.containsKey(station)) {
			path.add(0, station);
			station = prev.get(station);
		}
		path.add(0, station);

		String currentLine = subwayLineAtPrevStation.get(path.get(0));
		String currentTransferStation = " ";

		for (int i = 1; i < path.size(); i++) {
			String stationName = path.get(i);
			List<Edge> edges = adjacencyList.get(stationName);
			boolean transferRequired = true;
			double weight = Double.MAX_VALUE;

			if (transferRequired) {
				line.add(stationName + " (" + subwayLineAtPrevStation.get(stationName) + ")");
				currentTransferStation = path.get(i);
				i++;
			} else {
				path.add(i, "Take the " + currentLine + " to " + stationName + " (" + String.format("%.2f", weight)
						+ " km)");
			}
		}
		line.add(endStation);

		return line;
	}

	public static void main(String[] args) {

		try {
			MTARoute subway = new MTARoute();
			subway.SubwaySystem("D:\\MTA\\MTA\\mta_stations.csv");
			Scanner scan = new Scanner(System.in);
			System.out.print("Enter start station: ");
			String startStation = scan.nextLine();
			System.out.print("Enter end station: ");
			String endStation = scan.nextLine();
			System.out.println("Your Next Stops Are:");
			List<String> shortestPath = subway.bfs(startStation, endStation);
			System.out.println(shortestPath);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading file: " + e.getMessage());
		}
	}

}