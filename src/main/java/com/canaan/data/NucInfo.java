package com.canaan.data;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class NucInfo {
    private String result;

    private Result resultObj;

    private String pool;

    private String farmcode;

    private List<Worker> pools;

    private String worker;

    private String mac;

    private String nucCode;

    public void setResult(String result) {
        this.result = result;

        this.setResultObj(this.parseEStats(result));

    }

    /**
     * todo 优化
     */
    public Result parseEStats(String result) {
        Result resultObj = new Result();
        Map<String, Object> map = new HashMap<>();
        int count = 0;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '|') {
                count++;
            }
        }
        // 判断|标志是否出现两次，如果没有则信息不全
        if (count != 2) {
            return resultObj;
        }

        int mmStart = result.indexOf("MM ID");
        int mmEnd = result.lastIndexOf("]") + 1;
        if (mmStart >= result.length()) {
            return resultObj;
        }
        if (mmStart < 0) {
            //没有MM信息，即其他矿机
            return resultObj;
        }

        // mm
        String mm = result.substring(mmStart, mmEnd).trim();
        // mm之后


        String mmStr = mm.split("=")[1];
        // 解析mm板信息
        String workstatus = "", hashbord = "", fan = "", pll = "", sf = "", pvt_t = "", pvt_v = "", mwn = "", pout = "",
                atabd = "";
        String start = "]", end = "[";
        Pattern pattern = Pattern.compile("(?<=\\" + start + ")[^\\" + end + "]+");
        Matcher matcher = pattern.matcher(mmStr);
        while (matcher.find()) {
            String key = matcher.group().trim();
            int index = matcher.end();
            int s = mmStr.indexOf("[", index) + 1;
            int e = mmStr.indexOf("]", index);
            String value = mmStr.substring(s, e);
            if ("SYSTEMSTATU".equals(key)) {
                if (value.length() > 1) {
                    String[] arr = value.split(",");
                    workstatus = arr[0].split(":")[1].trim();
                    if (arr.length > 1) {
                        hashbord = arr[1].split(":")[1].trim();
                    }
                }
            } else if ("Fan1".equals(key) || "Fan2".equals(key) || "Fan3".equals(key) || "Fan4".equals(key)) {
                fan += value + ",";
            } else if (key.startsWith("PS")) {
                pout = value.split(" ")[0];
                map.put("pout", pout);
                map.put("ps", value);
            } else if (key.startsWith("PLL")) {
                if (key.length() == 4) {
                    pll += value + ",";
                }
                // PLL0 PLL1
                // PLLCNT0_0 PLLCNT0_1 PLLCNT0_2 PLLCNT0_3
                // PLLCNT1_0 PLLCNT1_1 PLLCNT1_2 PLLCNT1_3
            } else if (key.startsWith("SF")) {
                sf += value + ",";
            } else if (key.startsWith("PVT_T")) {
                pvt_t += value + ",";
            } else if (key.startsWith("PVT_V")) {
                pvt_v += value + ",";
            } else if (key.startsWith("MW") & key.length() > 2) {
                mwn += value + ",";
            } else if (key.startsWith("ATABD")) {
                atabd += value + ",";
            } else if (key.toLowerCase().startsWith("pows")) {
                if (value.contains(" ")) {
                    resultObj.setPows(value.split(" ")[0]);
                }
            } else if (key.toLowerCase().startsWith("hashs")) {
                if (value.contains(" ")) {
                    resultObj.setHashs(value.split(" ")[0]);
                }
            } else if (key.toLowerCase().startsWith("pools")) {
                if (value.contains(" ")) {
                    resultObj.setPools(value.split(" ")[0]);
                }
            } else {
                if (key.equals("Vo")) {
//					System.out.println("Vo"+value);
                }
                map.put(key.toLowerCase(), value.replace("%", ""));
            }
        }
        // 第一个参数ver
        int verIndex = mmStr.indexOf("Ver");
        int s = mmStr.indexOf("[", verIndex) + 1;
        int e = mmStr.indexOf("]", verIndex);
        String value = mmStr.substring(s, e);
        resultObj.setVer(value);
        resultObj.setMajorv(value.split("-")[0]);
        resultObj.setWorkstatus(workstatus);
        resultObj.setHashbord(hashbord);
        if (!fan.isEmpty()) {
            resultObj.setFan(fan.charAt(fan.length() - 1) == ',' ? fan.substring(0, fan.length() - 1) : fan);
        }
        if (!pll.isEmpty()) {
            resultObj.setPll(pll.charAt(pll.length() - 1) == ',' ? pll.substring(0, pll.length() - 1) : pll);
        }
        if (!sf.isEmpty()) {
            resultObj.setSf(sf.charAt(sf.length() - 1) == ',' ? sf.substring(0, sf.length() - 1) : sf);
        }
        if (!pvt_t.isEmpty()) {
            resultObj.setPvt_t(pvt_t.charAt(pvt_t.length() - 1) == ',' ? pvt_t.substring(0, pvt_t.length() - 1) : pvt_t);
        }
        if (!pvt_v.isEmpty()) {
            resultObj.setPvt_v(pvt_v.charAt(pvt_v.length() - 1) == ',' ? pvt_v.substring(0, pvt_v.length() - 1) : pvt_v);
        }
        if (!mwn.isEmpty()) {
            resultObj.setMwn(mwn.charAt(mwn.length() - 1) == ',' ? mwn.substring(0, mwn.length() - 1) : mwn);
        }
        if (!atabd.isEmpty()) {
            resultObj.setAtabd(atabd.charAt(atabd.length() - 1) == ',' ? atabd.substring(0, atabd.length() - 1) : atabd);
        }

        return resultObj;
    }
}
