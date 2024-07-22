package org.example.mtgspotscrapper.mtgapi;

import java.util.Collection;

public record ImportantCardData(String name, int convertedManaCost, Collection<String> colors, Collection<Character> colorIdentity, String type, Collection<String> cardTypes, String imageUrl) {
}