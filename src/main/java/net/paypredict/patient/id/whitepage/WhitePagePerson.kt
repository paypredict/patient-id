package net.paypredict.patient.id.whitepage

import kotlin.properties.ReadOnlyProperty

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/13/2018.
 */
class WhitePagePerson(
    val id: String,
    main: ReadOnlyProperty<WhitePagePerson, String?>,
    address: ReadOnlyProperty<WhitePagePerson, Address?>,
    phones: ReadOnlyProperty<WhitePagePerson, List<Phone>>
) {
    val name: String? by main
    val firstname: String? by main
    val middlename: String? by main
    val lastname: String? by main
    val age_range: String? by main
    val gender: String? by main

    val address: Address? by address
    val phones: List<Phone> by phones
}

class Address(
    val id: String,
    main: ReadOnlyProperty<Address, String?>
) {
    val street_line_1: String? by main
    val street_line_2: String? by main
    val city: String? by main
    val postal_code: String? by main
    val zip4: String? by main
    val state_code: String? by main
}

class Phone(
    val id: String,
    main: ReadOnlyProperty<Phone, String?>
) {
    val phone_number: String? by main
    val line_type: String? by main
}
