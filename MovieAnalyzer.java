import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class MovieAnalyzer {

    List<String> col;
    List<List<String>> line;
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public MovieAnalyzer(String dataset_path) {

        try (FileInputStream fis = new FileInputStream(dataset_path);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                col = Arrays.asList(firstLine.split(","));
                line = br.lines().map(MovieAnalyzer::parseLine).collect(toList());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char cusomQuote) {
        List<String> result = new ArrayList<>();
        if (cvsLine == null) {
            assert false;
            if (cvsLine.isEmpty()) {
                return result;
            }
        }
        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder curVal = new StringBuilder();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {
            if (inQuotes) {
                startCollectChar = true;
                if (ch == cusomQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }
                }
            } else {
                if (ch == cusomQuote) {
                    inQuotes = true;
                    if (chars[0] != '"' && cusomQuote == '\"') {
                        curVal.append('"');
                    }
                    if (startCollectChar) {
                        curVal.append('"');
                    }
                } else if (ch == separators) {
                    result.add(curVal.toString());

                    curVal = new StringBuilder();
                    startCollectChar = false;
                }  else if (ch == '\n') {
                    break;
                } else {
                    curVal.append(ch);
                }
            }
        }
        result.add(curVal.toString());
        return result;
    }

    public Map<Integer, Integer> getMovieCountByYear() {
        Map<Integer, Integer> maps = new TreeMap<>(
                Comparator.reverseOrder()
        );
        List<Integer> year_list = new ArrayList<>(line.size());
        for (List<String> strings : line) {
            year_list.add(Integer.parseInt(strings.get(2)));
        }
        year_list.forEach(y -> {
            Integer counts = maps.get(y);
            maps.put(y, counts == null ? 1 : ++counts);
        });
        return maps;
    }

    int findCharCount(String str) {
        int num = 0;
        char[] strchar;
        strchar = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (strchar[i] == ',')
                num++;
        }
        return num;
    }

    public Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> maps = new HashMap<>();
        List<String> geren_list1 = new ArrayList<>();
        for (List<String> strings : line) {
            geren_list1.add(strings.get(5).split(",")[0].trim());
        }
        List<String> geren_list2 = new ArrayList<>();
        for (List<String> strings : line) {
            if (strings.get(5).contains(","))
                geren_list2.add(strings.get(5).split(",")[1].trim());
        }
        List<String> geren_list3 = new ArrayList<>();
        for (List<String> strings : line) {
            if (findCharCount(strings.get(5)) >= 2)
                geren_list3.add(strings.get(5).split(",")[2].trim());
        }
        List<String> geren_list4 = new ArrayList<>();
        geren_list4.addAll(geren_list1);
        geren_list4.addAll(geren_list2);
        geren_list4.addAll(geren_list3);
        geren_list4.forEach(y -> {
            Integer counts = maps.get(y);
            maps.put(y, counts == null ? 1 : ++counts);
        });
        List<Map.Entry<String, Integer>> lstEntry = new ArrayList<>(maps.entrySet());
        lstEntry.sort((o1, o2) -> {
            int compare = o2.getValue().compareTo(o1.getValue());
            if (compare == 0) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return compare;
        });

        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        lstEntry.forEach(o -> linkedHashMap.put(o.getKey(), o.getValue()));
        return linkedHashMap;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        Map<String, Integer> map = new HashMap<>();
        List<String> star_list1 = new ArrayList<>();
        for (List<String> strings : line) {
            int compare = strings.get(10).compareTo(strings.get(11));
            if (compare < 0)
                star_list1.add(strings.get(10) + "," + strings.get(11));
            else
                star_list1.add(strings.get(11) + "," + strings.get(10));
        }
        List<String> star_list2 = new ArrayList<>(line.size());
        for (List<String> strings : line) {
            int compare = strings.get(10).compareTo(strings.get(12));
            if (compare < 0)
                star_list2.add(strings.get(10) + "," + strings.get(12));
            else
                star_list2.add(strings.get(12) + "," + strings.get(10));
        }
        List<String> star_list3 = new ArrayList<>(line.size());
        for (List<String> strings : line) {
            int compare = strings.get(10).compareTo(strings.get(13));
            if (compare < 0)
                star_list3.add(strings.get(10) + "," + strings.get(13));
            else
                star_list3.add(strings.get(13) + "," + strings.get(10));
        }
        List<String> star_list4 = new ArrayList<>(line.size());
        for (List<String> strings : line) {
            int compare = strings.get(11).compareTo(strings.get(12));
            if (compare < 0)
                star_list4.add(strings.get(11) + "," + strings.get(12));
            else
                star_list4.add(strings.get(12) + "," + strings.get(11));
        }
        List<String> star_list5 = new ArrayList<>(line.size());
        for (List<String> strings : line) {
            int compare = strings.get(11).compareTo(strings.get(13));
            if (compare < 0)
                star_list5.add(strings.get(11) + "," + strings.get(13));
            else
                star_list5.add(strings.get(13) + "," + strings.get(11));
        }
        List<String> star_list6 = new ArrayList<>(line.size());
        for (List<String> strings : line) {
            int compare = strings.get(12).compareTo(strings.get(13));
            if (compare < 0)
                star_list6.add(strings.get(12) + "," + strings.get(13));
            else
                star_list6.add(strings.get(13) + "," + strings.get(12));
        }


        List<String> star_list = new ArrayList<>();
        for (String s : star_list1) {
            star_list.add(s.trim());
        }
        for (String s : star_list2) {
            star_list.add(s.trim());
        }
        for (String s : star_list3) {
            star_list.add(s.trim());
        }
        for (String s : star_list4) {
            star_list.add(s.trim());
        }
        for (String s : star_list5) {
            star_list.add(s.trim());
        }
        for (String s : star_list6) {
            star_list.add(s.trim());
        }

        star_list.forEach(y -> {
            Integer counts = map.get(y);
            map.put(y, counts == null ? 1 : ++counts);
        });
        List<Map.Entry<String, Integer>> lstEntry = new ArrayList<>(map.entrySet());
        lstEntry.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        LinkedHashMap<List<String>, Integer> linkedHashMap = new LinkedHashMap<>();
        lstEntry.forEach(o -> {
            List<String> temp = new ArrayList<>();
            temp.add(o.getKey().split(",")[0].trim());
            temp.add(o.getKey().split(",")[1].trim());
            linkedHashMap.put(temp, o.getValue());
        });
        return linkedHashMap;
    }

    public static class UserDTO implements Comparable<UserDTO> {
        private final String movies;
        private final Integer v1;
        private final Integer v2;

        public UserDTO(String movies, Integer v1, Integer v2) {
            this.movies = movies;
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        public int compareTo(UserDTO userDTO) {
            return userDTO.getV1() - this.v1;
        }

        private String getMovies() {
            return movies;
        }

        private int getV1() {
            return v1;
        }

        private int getV2() {
            return v2;
        }

    }

    public List<String> getTopMovies(int top_k, String by) {
        List<UserDTO> key_list = new ArrayList<>();

        for (List<String> strings : line) {
            key_list.add(new UserDTO(strings.get(1), Integer.parseInt(strings.get(4).trim().replace(" min", "")), strings.get(7).replaceAll("\"", "\"\"").strip().length()));
        }
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).get(1).equals("3 Idiots")||line.get(i).get(1).equals("Indiana Jones and the Last Crusade")) {
                System.out.println(line.get(i).get(7));
                System.out.println(key_list.get(i).v1);
            }}
        key_list.sort((o1, o2) -> {
            int num = o2.getV1() - o1.getV1();
            if (num == 0) {
                return o1.getMovies().compareTo(o2.getMovies());
            }
            return num;
        });


        List<String> list1 = new ArrayList<>();
        for (UserDTO userDTO : key_list) {
            list1.add(userDTO.movies);
        }
        key_list.sort((o1, o2) -> {
            int num = o2.getV2() - o1.getV2();
            if (num == 0) {
                return o1.getMovies().compareTo(o2.getMovies());
            }
            return num;
        });
        List<String> list2 = new ArrayList<>();
        for (UserDTO userDTO : key_list) {
            list2.add(userDTO.movies);
        }
        List<String> listByRuntime = list1.subList(0, top_k);
        List<String> listByOverview = list2.subList(0, top_k);

        if (Objects.equals(by, "runtime")) {
            return listByRuntime;
        }
        return listByOverview;
    }

    public List<String> getTopStars(int top_k, String by) {
        List<String> list = new ArrayList<>();
        for (List<String> strings : line) {
            for (int j = 0; j < 4; j++) {
                if (!list.contains(strings.get(10 + j)))
                    list.add(strings.get(10 + j));
            }
        }
        Map<String, Double> mapByStars = new TreeMap<>();

        float count = 0;
        double sum = 0;
        double average_rating;
        for (String s : list) {
            for (List<String> strings : line) {
                if (strings.subList(10, 14).contains(s)) {
                    count += 1;
                    sum += Float.parseFloat(strings.get(6));
                }
            }
            if (count != 0) {
                average_rating = sum / count;
                mapByStars.put(s, average_rating);
            }
            sum = 0.0000000;
            count = 0;

        }
        List<Map.Entry<String, Double>> lstEntry = new ArrayList<>(mapByStars.entrySet());
        lstEntry.sort((o1, o2) -> {
            int compare = o2.getValue().compareTo(o1.getValue());
            if (compare == 0) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return compare;
        });

        LinkedHashMap<String, Double> linkedHashMap2 = new LinkedHashMap<>();
        lstEntry.forEach(o -> linkedHashMap2.put(o.getKey(), o.getValue()));

        List<String> listByRating = new ArrayList<>(linkedHashMap2.keySet());

        listByRating = listByRating.subList(0, top_k);


//        System.out.println(line.get(0).subList(10,14)+"||"+stars_list.get(0));
        int count1 = 0;
        long sum1 = 0;
        double average_gross;
        Map<String, Double> mapByStars1 = new TreeMap<>();
        for (String s : list) {
            for (List<String> strings : line) {
                if (strings.subList(10, 14).contains(s) && !Objects.equals(strings.get(15), "")) {
                    count1 += 1;
                    sum1 += Long.parseLong(strings.get(15).replace(",", ""));
                }
            }
            if (count1 != 0) {
                average_gross = (double) sum1 / count1;
                mapByStars1.put(s, average_gross);
                sum1 = 0;
                count1 = 0;
            } else {
                sum1 = 0;
            }

//            if (list.get(i).equals("Bob Peterson")){
//                System.out.println(average_gross);
//            }

        }

        List<Map.Entry<String, Double>> lstEntry1 = new ArrayList<>(mapByStars1.entrySet());
        lstEntry1.sort((o1, o2) -> {
            int compare = o2.getValue().compareTo(o1.getValue());
            if (compare == 0) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return compare;
        });

        LinkedHashMap<String, Double> linkedHashMap1 = new LinkedHashMap<>();
        lstEntry1.forEach(o -> linkedHashMap1.put(o.getKey(), o.getValue()));

        List<String> listByGross = new ArrayList<>(linkedHashMap1.keySet());

        listByGross = listByGross.subList(0, top_k);

        if (Objects.equals(by, "rating"))
            return listByRating;
        else
            return listByGross;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        Map<String, Double> map1 = new TreeMap<>();
        for (List<String> strings : line) {
            map1.put(strings.get(1), Double.valueOf(strings.get(6)));
        }
        List<String> Movie_List1 = new ArrayList<>();
        for (String key : map1.keySet()) {
            if (map1.get(key) >= min_rating) {
                Movie_List1.add(key);
            }
        }
        Map<String, Double> map2 = new TreeMap<>();
        for (List<String> strings : line) {
            map2.put(strings.get(1), Double.valueOf(strings.get(4).split(" ")[0]));
        }
        List<String> Movie_List2 = new ArrayList<>();
        for (String key : map2.keySet()) {
            if (map2.get(key) <= max_runtime) {
                Movie_List2.add(key);
            }
        }
        Map<String, String> map3 = new TreeMap<>();
        for (List<String> strings : line) {
            map3.put(strings.get(1), strings.get(5));
        }
        List<String> Movie_List3 = new ArrayList<>();
        for (String key : map3.keySet()) {
            if (map3.get(key).contains(genre)) {
                Movie_List3.add(key);
            }
        }
        List<String> intersection1 = Movie_List1.stream().filter(Movie_List2::contains).toList();
        return intersection1.stream().filter(Movie_List3::contains).sorted().collect(toList());

    }
    
}

