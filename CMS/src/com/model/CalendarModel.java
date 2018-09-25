package com.model;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * CalendarModel
 * Usage:
 *
 * @author dgeng
 * create time 2018/9/25 - 上午8:21
 */
public class CalendarModel {

    public static HashMap<String, CalendarPO> calendarPOMap;

    public static void init() {
        calendarPOMap = new HashMap<>();
        File file = new File("data");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
//            JSONObject object = JSONArray.parseObject(new FileInputStream(file), CalendarPO.class);
            BufferedReader inputStream = new BufferedReader(new FileReader(file));
            String line;
            while ((line = inputStream.readLine()) != null) {
                if (line.length() > 0) {
                    CalendarPO po = JSONObject.parseObject(line, CalendarPO.class);
//                    String[] parts = line.split("\\s+");
//                    CalendarPO po = new CalendarPO();
//                    po.setName(parts[0]);
//                    po.setStartDay(FORMATTER.parse(parts[1]));
//                    po.setEndDay(FORMATTER.parse(parts[2]));
//                    po.setStartTime(parts[3]);
//                    po.setEndTime(parts[4]);
//                    po.setDurationOption(Integer.parseInt(parts[5]));
                    calendarPOMap.put(po.getName(), po);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertCalendar(CalendarPO po) {
        if (calendarPOMap.containsKey(po.getName())) {
            return false;
        }
        calendarPOMap.put(po.getName(), po);
        save();
        return true;
    }


    public static void save() {
        try {
            PrintWriter writer = new PrintWriter(new File("data"));
            for (CalendarPO each : calendarPOMap.values()) {
//                writer.println(each.getName() + " " + FORMATTER.format(each.getStartDay()) + " " + FORMATTER.format(each.getEndDay()) + " "
//                        + each.getStartTime() + " " + each.getEndTime() + " " + each.getDurationOption());
                writer.println(JSONObject.toJSONString(each));
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
