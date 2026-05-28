import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class CO5Assignment {

    static class Artist {
        String name;
        long listeners;

        Artist(String n, long l) { 
            this.name = n; 
            this.listeners = l; 
        }

        @Override
        public String toString() {
            return String.format("%s (%dM)", name, listeners);
        }
    }

    /** * Returns the top-k artists by listeners, sorted descending. 
     */
    static List<Artist> topK(List<Artist> all, int k) {
        // Min-heap by listeners: peek is the smallest of the current top-k.
        PriorityQueue<Artist> minHeap = new PriorityQueue<>(
            (a, b) -> Long.compare(a.listeners, b.listeners)
        );

        for (Artist artist : all) {
            if (minHeap.size() < k) {
                minHeap.offer(artist);
            } 
            else if (artist.listeners > minHeap.peek().listeners) {
                minHeap.poll();
                minHeap.offer(artist);
            }
        }

        // Extract heap into a list and sort descending by listeners.
        List<Artist> result = new ArrayList<>(minHeap);
        result.sort((a, b) -> Long.compare(b.listeners, a.listeners));
        
        return result;
    }

    public static void main(String[] args) {
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("Artist1", 45));
        artists.add(new Artist("Artist2", 12));
        artists.add(new Artist("Artist3", 78));
        artists.add(new Artist("Artist4", 23));
        artists.add(new Artist("Artist5", 56));
        artists.add(new Artist("Artist6", 89));
        artists.add(new Artist("Artist7", 34));
        artists.add(new Artist("Artist8", 67));
        artists.add(new Artist("Artist9", 18));
        artists.add(new Artist("Artist10", 91));
        artists.add(new Artist("Artist11", 50));
        artists.add(new Artist("Artist12", 39));

        int k = 5;
        System.out.println("Processing " + artists.size() + " artists to find the top " + k + "...\n");
        
        List<Artist> topArtists = topK(artists, k);

        // Print final leaderboard result
        System.out.println("--- Final Top 5 Leaderboard ---");
        for (int i = 0; i < topArtists.size(); i++) {
            System.out.println((i + 1) + ". " + topArtists.get(i));
        }
    }
}