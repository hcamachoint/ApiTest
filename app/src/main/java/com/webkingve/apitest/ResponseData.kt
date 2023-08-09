package com.webkingve.apitest

data class ResponseData (
    val id: Int,
    val message: String,
    val details: Details,
    val fruits: List<FruitListDetails>
)

data class Details(
    val first_name: String,
    val last_name: String
)

data class FruitListDetails(
    val name: String,
    val color: String
)