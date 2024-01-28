package com.example;
import java.util.ArrayList;
import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        System.out.print("Please enter an airport ICAO: ");
        Scanner in = new Scanner(System.in);
        String icao = in.nextLine();
        while(icao.length() != 4) {
            System.out.print("\nPlease enter a valid airport ICAO: ");
            icao = in.nextLine();
        }
        double[] coordinates = AirportLocator.getLocation(icao);
        System.out.println("Coordinates of " + icao.toUpperCase() + ": " + coordinates[0] + ", " + coordinates[1]);
        ArrayList<String> metar = CoordinateWeather.getWeather(icao, coordinates);
        for(int i = 0; i < metar.size() - 1; i++) {
            System.out.print(metar.get(i) + " ");
        }
        System.out.print(metar.get(metar.size() - 1));
    }
}
