package com.example.materialwall.presentation.ui

sealed class Category(val name: String, val url: String) {
    object CategoryBackground : Category(
        "Background",
        "https://cdn.pixabay.com/photo/2018/08/14/13/23/ocean-3605547_1280.jpg"
    )

    object CategoryNature : Category(
        "Nature",
        "https://cdn.pixabay.com/photo/2017/12/15/13/51/polynesia-3021072_1280.jpg"
    )

    object CategoryArt :
        Category("Art", "https://cdn.pixabay.com/photo/2017/12/28/16/18/bicycle-3045580_1280.jpg")

    object CategoryAbstract : Category(
        "Abstract",
        "https://cdn.pixabay.com/photo/2019/04/26/17/47/color-4158152_1280.jpg"
    )

    object CategoryAnimal : Category(
        "Animal",
        "https://cdn.pixabay.com/photo/2016/11/14/04/45/elephant-1822636_1280.jpg"
    )

    object CategoryScience : Category(
        "Science",
        "https://cdn.pixabay.com/photo/2021/08/10/10/06/pinwheels-6535595_1280.jpg"
    )

    object CategoryPeople :
        Category("People", "https://cdn.pixabay.com/photo/2017/08/07/21/40/people-2608145_1280.jpg")

    object CategoryPlaces : Category(
        "Places",
        "https://cdn.pixabay.com/photo/2020/02/04/04/24/watercolour-4817271_1280.jpg"
    )

    object CategorySports : Category(
        "Sports",
        "https://cdn.pixabay.com/photo/2022/01/09/13/58/the-vehicle-6926029_1280.jpg"
    )

    object CategoryMonochrome : Category(
        "MonoChrome",
        "https://cdn.pixabay.com/photo/2021/11/16/18/10/nature-6801719_1280.jpg"
    )

    object CategoryList {
        val list = listOf(
            CategoryMonochrome,
            CategoryNature,
            CategoryBackground,
            CategoryArt,
            CategoryAbstract,
            CategoryAnimal,
            CategoryScience,
            CategoryPeople,
            CategoryPlaces,
            CategorySports
        )
    }
}
