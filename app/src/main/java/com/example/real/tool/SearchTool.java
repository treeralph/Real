package com.example.real.tool;

import java.util.ArrayList;

public class SearchTool {

    public static final String[] category = {"디지털기기", "생활가전", "가구/인테리어", "유아", "유아도서",
                                             "생활/가공식품", "스포츠/레저", "여성잡화", "여성의류",
                                             "남성패션/잡화", "게임/취미", "뷰티/미용", "반려동물용품",
                                             "도서/티켓/음반", "식물", "기타 중고물품", "중고차", "이벤트"};

    public static final String category_digitalDevice = "디지털기기";
    public static final String category_digitalHomeDevice = "생활가전";
    public static final String category_furnitureInterior = "가구/인테리어";
    public static final String category_infant = "유아";
    public static final String category_infantBook = "유아도서";
    public static final String category_food = "생활/가공식품";
    public static final String category_sport = "스포츠/레저";
    public static final String category_womenEtc = "여성잡화";
    public static final String category_womenClothing = "여성의류";
    public static final String category_menClothing = "남성패션/잡화";
    public static final String category_gameHobby = "게임/취미";
    public static final String category_beauty = "뷰티/미용";
    public static final String category_petStuff = "반려동물용품";
    public static final String category_bookTicketEtc = "도서/티켓/음반";
    public static final String category_plant = "식물";
    public static final String category_etc = "기타 중고물품";
    public static final String category_car = "중고차";
    public static final String category_event = "이벤트";

    public SearchTool(){

    }

    public ArrayList<String> makeCase(ArrayList<String> wordList){

        ArrayList<String> caseList = new ArrayList<>();

        for(String word: wordList) {
            int length = word.length();
            for (int i = 1; i <= length; i++) {
                for (int j = 0; j < length; j++) {
                    try {
                        String subString = word.substring(j, j + i);
                        if(subString.isEmpty()){
                            continue;
                        }
                        caseList.add(subString);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
        return caseList;
    }
}
