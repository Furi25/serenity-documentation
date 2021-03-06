package net.serenity_bdd.samples.etsy.features.steps.serenity;

import com.google.common.base.Optional;
import net.serenity_bdd.samples.etsy.features.model.ListingItem;
import net.serenity_bdd.samples.etsy.features.model.OrderCostSummary;
import net.serenity_bdd.samples.etsy.pages.CartPage;
import net.serenity_bdd.samples.etsy.pages.HomePage;
import net.serenity_bdd.samples.etsy.pages.ItemDetailsPage;
import net.serenity_bdd.samples.etsy.pages.SearchResultsPage;
import net.thucydides.core.annotations.Step;
import org.assertj.core.data.Offset;
import org.hamcrest.Matcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// tag::header[]
public class BuyerSteps {
// end::header[]
// tag::searchByKeywordSteps[]

    HomePage homePage;                                          // <1>
    SearchResultsPage searchResultsPage;
    CartPage cartPage;

    @Step                                                       // <2>
    public void opens_etsy_home_page() {
        homePage.open();
    }

    @Step
    public void searches_for_items_containing(String keywords) {
        homePage.searchFor(keywords);
    }

    @Step
    public void should_see_items_related_to(String keywords) {
        List<String> resultTitles = searchResultsPage.getResultTitles();
        resultTitles.stream().forEach(title -> assertThat(title.contains(keywords)));
    }
// end::searchByKeywordSteps[]
// tag::filterByType[]
    @Step
    public void filters_results_by_type(String type) {
        searchResultsPage.filterByType(type);
    }

    public int get_matching_item_count() {
        return searchResultsPage.getItemCount();
    }

    @Step
    public void should_see_item_count(Matcher<Integer> itemCountMatcher) {
        itemCountMatcher.matches(searchResultsPage.getItemCount());
    }


// end::filterByType[]

    ItemDetailsPage detailsPage;

    @Step
    public void selects_item_number(int number) {
        searchResultsPage.selectItem(number);
    }

    @Step
    public void should_see_matching_details(String searchTerm) {
        String itemName = detailsPage.getItemName();
        assertThat(itemName.toLowerCase()).contains(searchTerm);
    }

    @Step
    public void should_see_items_of_type(String type) {
        Optional<String> selectedType = searchResultsPage.getSelectedType();
        assertThat(selectedType.isPresent()).describedAs("No item type was selected").isTrue();
        assertThat(selectedType.get()).isEqualTo(type);
    }

    @Step
    public void selects_any_product_variations() {
        detailsPage.getProductVariationIds().stream()
                .forEach(id -> detailsPage.selectVariation(id,2));
    }

    @Step
    public void should_see_item_in_cart(ListingItem selectedItem) {
        assertThat(cartPage.getOrderCostSummaries()
                .stream().anyMatch(order -> order.getName().equals(selectedItem.getName()))).isTrue();
    }

    @Step
    public void should_see_total_including_shipping_for(ListingItem selectedItem) {
        OrderCostSummary orderCostSummary
                = cartPage.getOrderCostSummaryFor(selectedItem).get();

        double itemTotal = orderCostSummary.getItemTotal();
        double shipping = orderCostSummary.getShipping();
        double totalCost = orderCostSummary.getTotalCost();

        assertThat(itemTotal).isEqualTo(selectedItem.getPrice());
        assertThat(shipping).isGreaterThan(0.0);
        assertThat(totalCost).isCloseTo(itemTotal + shipping, Offset.offset(0.001));
    }

    @Step
    public void adds_current_item_to_shopping_cart() {
        detailsPage.addToCart();
    }


// tag::tail[]
}
//end:tail
