import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.serverless.Recipe
import com.serverless.RecipeService
import junit.framework.Assert.assertEquals
import org.junit.Test

val client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
        AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
        .build()

class RecipeServiceTests {

    val service = RecipeService(client)

    @Test
    fun createTableWorks() {
//        service.createTable()
    }

    @Test
    fun basicCrudWorks() {
        val recipe = Recipe("t1", "tag1", "i", "d")
        val updateRecipe = Recipe("t1", "ut", "ui", "di")
        val secondRecipe = Recipe("t2", "t", "i", "d", "i")

        assertEquals(service.getItems().size, 0)
        service.addItem(recipe)

        assertEquals(service.getItems().first().title, "t1")
        assertEquals(service.getItems().first().tags, "tag1")
        assertEquals(service.getItems().size, 1)

        service.addItem(updateRecipe)
        assertEquals(service.getItems().first().tags, "ut")

        service.addItem(secondRecipe)
        assertEquals(service.getItems().size, 2)

        service.deleteItem("t1")
        service.deleteItem("t2")
        assertEquals(service.getItems().size, 0)
    }

    @Test
    fun getTestServiceResponse() {
        service.addItem( Recipe("t1", "tag1 tag2", "i", "d"))
        service.addItem( Recipe("t2", "tag1 tag3 tag4", "i", "d"))
        val response = service.getRecipeResponse()
        assertEquals(response.recipes.size, 2)
        assertEquals(response.tags.size, 4)
        service.deleteItem("t1")
        service.deleteItem("t2")
    }


}