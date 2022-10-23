import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
                line = br.lines().map(line -> parseLine(line)).collect(toList());
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
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }
        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
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

                    curVal = new StringBuffer();
                    startCollectChar = false;
                } else if (ch == '\r') {
                    continue;
                } else if (ch == '\n') {
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
        Map<Integer, Integer> maps = new TreeMap<Integer, Integer>(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                }
        );
        List<Integer> year_list = new ArrayList<Integer>(line.size());
        for (int i = 0; i < line.size(); i++) {
            year_list.add(Integer.parseInt(line.get(i).get(2).toString()));
        }
        year_list.forEach(y -> {
            Integer counts = maps.get(y);
            maps.put(y, counts == null ? 1 : ++counts);

        });
        return maps;
    }

    int findCharCount(String str) {
        int num = 0;
        char[] strchar = new char[str.length()];
        strchar = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (strchar[i] == ',')
                num++;
        }
        return num;
    }

    public Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> maps = new HashMap<String, Integer>();
        List<String> geren_list1 = new ArrayList<String>();
        for (int i = 0; i < line.size(); i++) {
            geren_list1.add(line.get(i).get(5).split(",")[0].trim());
        }
        List<String> geren_list2 = new ArrayList<String>();
        for (int j = 0; j < line.size(); j++) {
            if (line.get(j).get(5).contains(","))
                geren_list2.add(line.get(j).get(5).split(",")[1].trim());
        }
        List<String> geren_list3 = new ArrayList<String>();
        for (int j = 0; j < line.size(); j++) {
            if (findCharCount(line.get(j).get(5).toString()) >= 2)
                geren_list3.add(line.get(j).get(5).split(",")[2].trim());
        }
        List<String> geren_list4 = new ArrayList<String>();
        for (int k = 0; k < geren_list1.size(); k++) {
            geren_list4.add(geren_list1.get(k));
        }
        for (int h = 0; h < geren_list2.size(); h++) {
            geren_list4.add(geren_list2.get(h));
        }
        for (int h = 0; h < geren_list3.size(); h++) {
            geren_list4.add(geren_list3.get(h));
        }
        geren_list4.forEach(y -> {
            Integer counts = maps.get(y);
            maps.put(y, counts == null ? 1 : ++counts);
        });
        List<Map.Entry<String, Integer>> lstEntry = new ArrayList<Map.Entry<String, Integer>>(maps.entrySet());
        Collections.sort(lstEntry, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int compare = o2.getValue().compareTo(o1.getValue());
                if (compare == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return compare;
            }
        });

        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        lstEntry.forEach(o -> {
            linkedHashMap.put(o.getKey(), o.getValue());
        });
        return linkedHashMap;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        List<String> star_list1 = new ArrayList<String>();
        for (int i = 0; i < line.size(); i++) {
            int compare = line.get(i).get(10).compareTo(line.get(i).get(11));
            if (compare < 0)
                star_list1.add(line.get(i).get(10) + "," + line.get(i).get(11));
            else
                star_list1.add(line.get(i).get(11) + "," + line.get(i).get(10));
        }
        List<String> star_list2 = new ArrayList<String>(line.size());
        for (int i = 0; i < line.size(); i++) {
            int compare = line.get(i).get(10).compareTo(line.get(i).get(12));
            if (compare < 0)
                star_list2.add(line.get(i).get(10) + "," + line.get(i).get(12));
            else
                star_list2.add(line.get(i).get(12) + "," + line.get(i).get(10));
        }
        List<String> star_list3 = new ArrayList<String>(line.size());
        for (int i = 0; i < line.size(); i++) {
            int compare = line.get(i).get(10).compareTo(line.get(i).get(13));
            if (compare < 0)
                star_list3.add(line.get(i).get(10) + "," + line.get(i).get(13));
            else
                star_list3.add(line.get(i).get(13) + "," + line.get(i).get(10));
        }
        List<String> star_list4 = new ArrayList<String>(line.size());
        for (int i = 0; i < line.size(); i++) {
            int compare = line.get(i).get(11).compareTo(line.get(i).get(12));
            if (compare < 0)
                star_list4.add(line.get(i).get(11) + "," + line.get(i).get(12));
            else
                star_list4.add(line.get(i).get(12) + "," + line.get(i).get(11));
        }
        List<String> star_list5 = new ArrayList<String>(line.size());
        for (int i = 0; i < line.size(); i++) {
            int compare = line.get(i).get(11).compareTo(line.get(i).get(13));
            if (compare < 0)
                star_list5.add(line.get(i).get(11) + "," + line.get(i).get(13));
            else
                star_list5.add(line.get(i).get(13) + "," + line.get(i).get(11));
        }
        List<String> star_list6 = new ArrayList<String>(line.size());
        for (int i = 0; i < line.size(); i++) {
            int compare = line.get(i).get(12).compareTo(line.get(i).get(13));
            if (compare < 0)
                star_list6.add(line.get(i).get(12) + "," + line.get(i).get(13));
            else
                star_list6.add(line.get(i).get(13) + "," + line.get(i).get(12));
        }


        List<String> star_list = new ArrayList<String>();
        for (int i = 0; i < star_list1.size(); i++) {
            star_list.add(star_list1.get(i).trim());
        }
        for (int i = 0; i < star_list2.size(); i++) {
            star_list.add(star_list2.get(i).trim());
        }
        for (int i = 0; i < star_list3.size(); i++) {
            star_list.add(star_list3.get(i).trim());
        }
        for (int i = 0; i < star_list4.size(); i++) {
            star_list.add(star_list4.get(i).trim());
        }
        for (int i = 0; i < star_list5.size(); i++) {
            star_list.add(star_list5.get(i).trim());
        }
        for (int i = 0; i < star_list6.size(); i++) {
            star_list.add(star_list6.get(i).trim());
        }

        star_list.forEach(y -> {
            Integer counts = map.get(y);
            map.put(y, counts == null ? 1 : ++counts);
        });
        List<Map.Entry<String, Integer>> lstEntry = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(lstEntry, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());

            }
        });

        LinkedHashMap<List<String>, Integer> linkedHashMap = new LinkedHashMap<>();
        lstEntry.forEach(o -> {
            List<String> temp = new ArrayList<>();
            temp.add(o.getKey().split(",")[0].trim());
            temp.add(o.getKey().split(",")[1].trim());
            linkedHashMap.put(temp, o.getValue());
            temp = null;
        });
        return linkedHashMap;
    }

    public class UserDTO implements Comparable<UserDTO> {
        private String movies;
        private Integer v1;
        private Integer v2;

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

        for (int i = 0; i < line.size(); i++) {
            key_list.add(new UserDTO(line.get(i).get(1), Integer.parseInt(line.get(i).get(4).trim().replace(" min", "")), line.get(i).get(7).replaceAll("\"", "\"\"").strip().length()));
        }
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).get(1).equals("3 Idiots")||line.get(i).get(1).equals("Indiana Jones and the Last Crusade")) {
                System.out.println(line.get(i).get(7));
                System.out.println(key_list.get(i).v1);
            }}
        Collections.sort(key_list, new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO o1, UserDTO o2) {
                int num = o2.getV1() - o1.getV1();
                if (num == 0) {
                    return o1.getMovies().compareTo(o2.getMovies());
                }
                return num;
            }
        });


        List<String> list1 = new ArrayList<String>();
        for (int i = 0; i < key_list.size(); i++) {
            list1.add(key_list.get(i).movies);
        }
        Collections.sort(key_list, new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO o1, UserDTO o2) {
                int num = o2.getV2() - o1.getV2();
                if (num == 0) {
                    return o1.getMovies().compareTo(o2.getMovies());
                }
                return num;
            }
        });
        List<String> list2 = new ArrayList<>();
        for (int i = 0; i < key_list.size(); i++) {
            list2.add(key_list.get(i).movies);
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
        for (int i = 0; i < line.size(); i++) {
            for (int j = 0; j < 4; j++) {
                if (!list.contains(line.get(i).get(10 + j)))
                    list.add(line.get(i).get(10 + j));
            }
        }
        Map<String, Double> mapByStars = new TreeMap<String, Double>();

        float count = 0;
        double sum = 0;
        double average_rating = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < line.size(); j++) {
                if (line.get(j).subList(10, 14).contains(list.get(i))) {
                    count += 1;
                    sum += Float.parseFloat(line.get(j).get(6));
                }
            }
            if (count != 0) {
                average_rating = sum / count;
                mapByStars.put(list.get(i), average_rating);
            }
            sum = 0.0000000;
            count = 0;

        }
        List<Map.Entry<String, Double>> lstEntry = new ArrayList<Map.Entry<String, Double>>(mapByStars.entrySet());
        Collections.sort(lstEntry, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                int compare = o2.getValue().compareTo(o1.getValue());
                if (compare == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return compare;
            }
        });

        LinkedHashMap<String, Double> linkedHashMap2 = new LinkedHashMap<>();
        lstEntry.forEach(o -> {
            linkedHashMap2.put(o.getKey(), o.getValue());
        });

        List<String> listByRating = new ArrayList<>(linkedHashMap2.keySet());

        listByRating = listByRating.subList(0, top_k);


//        System.out.println(line.get(0).subList(10,14)+"||"+stars_list.get(0));
        int count1 = 0;
        long sum1 = 0;
        double average_gross = 0;
        Map<String, Double> mapByStars1 = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < line.size(); j++) {
                if (line.get(j).subList(10, 14).contains(list.get(i)) && !Objects.equals(line.get(j).get(15), "")) {
                    count1 += 1;
                    sum1 += Long.parseLong(line.get(j).get(15).replace(",", ""));
                }
            }
            if (count1 != 0) {
                average_gross = (double) sum1 / count1;
                mapByStars1.put(list.get(i), average_gross);
                sum1 = 0;
                count1 = 0;
            } else {
                sum1 = 0;
            }

//            if (list.get(i).equals("Bob Peterson")){
//                System.out.println(average_gross);
//            }

        }

        List<Map.Entry<String, Double>> lstEntry1 = new ArrayList<Map.Entry<String, Double>>(mapByStars1.entrySet());
        Collections.sort(lstEntry1, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                int compare = o2.getValue().compareTo(o1.getValue());
                if (compare == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return compare;
            }
        });

        LinkedHashMap<String, Double> linkedHashMap1 = new LinkedHashMap<>();
        lstEntry1.forEach(o -> {
            linkedHashMap1.put(o.getKey(), o.getValue());
        });

        List<String> listByGross = new ArrayList<>(linkedHashMap1.keySet());

        listByGross = listByGross.subList(0, top_k);

        if (Objects.equals(by, "rating"))
            return listByRating;
        else
            return listByGross;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        Map<String, Double> map1 = new TreeMap<String, Double>();
        for (List<String> strings : line) {
            map1.put(strings.get(1), Double.valueOf(strings.get(6)));
        }
        List<String> Movie_List1 = new ArrayList<>();
        for (String key : map1.keySet()) {
            if (map1.get(key) >= min_rating) {
                Movie_List1.add(key);
            }
        }
        Map<String, Double> map2 = new TreeMap<String, Double>();
        for (List<String> strings : line) {
            map2.put(strings.get(1), Double.valueOf(strings.get(4).split(" ")[0]));
        }
        List<String> Movie_List2 = new ArrayList<>();
        for (String key : map2.keySet()) {
            if (map2.get(key) <= max_runtime) {
                Movie_List2.add(key);
            }
        }
        Map<String, String> map3 = new TreeMap<String, String>();
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
        List<String> intersection2 = intersection1.stream().filter(Movie_List3::contains).collect(toList());
        Collections.sort(intersection2);
        return intersection2;

    }


    public static <String, V extends Comparable<? super V>> Map<String, V> sortDescend(Map<String, V> map) {
        List<Map.Entry<String, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, V>>() {
            @Override
            public int compare(Map.Entry<String, V> o1, Map.Entry<String, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });
        Map<String, V> returnMap = new LinkedHashMap<String, V>();
        for (Map.Entry<String, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }

    public String handleCsvComma(String str) {
        StringBuilder sb = new StringBuilder();
        String handleStr = str;

        if (str.contains(",")) {

            if (str.contains("\"")) {
                handleStr = str.replace("\"", "\"\"");
            }

            handleStr = "\"" + handleStr + "\"";
        }

        return sb.append(handleStr).append(",").toString();
    }

    public static Map<String, Integer> sortMapByKey(Map<String, Integer> map) {
        Map<String, Integer> treemap = new TreeMap<String, Integer>(map);
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(treemap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return treemap;
    }

}

