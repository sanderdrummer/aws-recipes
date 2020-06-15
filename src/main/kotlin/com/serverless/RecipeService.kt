package com.serverless

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.PrimaryKey
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.*

val TABLE = "recipes-table"

data class GetRecipeResponse(val recipes: List<Recipe>, val tags: List<String>)

class RecipeService(val client: AmazonDynamoDB) {
    val dynamoDB = DynamoDB(client)
    fun createTable() {
        val table = dynamoDB.createTable(TABLE,
                listOf(KeySchemaElement().withAttributeName("title").withKeyType(KeyType.HASH)),
                listOf(AttributeDefinition().withAttributeName("title").withAttributeType(ScalarAttributeType.S)),
                ProvisionedThroughput(1L, 1L)
        )
        table.waitForActive()
    }

    fun deleteItem(title: String) {
        val table = dynamoDB.getTable(TABLE)
        table.deleteItem(PrimaryKey("title", title))
    }

    fun addItem(recipe: Recipe): Recipe {
        val table = dynamoDB.getTable(TABLE)
        table.putItem(Item()
                .withPrimaryKey(PrimaryKey("title", recipe.title))
                .withString("tags", recipe.tags)
                .withString("ingredients", recipe.ingredients)
                .withString("description", recipe.description)
        )
        return recipe
    }

    fun updateItem(recipe: Recipe) {
        val table = dynamoDB.getTable(TABLE)
        val updateSpec = UpdateItemSpec().withPrimaryKey("title", recipe.title)
                .withUpdateExpression("set tags = :tags")
                .withValueMap(ValueMap().withString(":tags", recipe.tags))
        table.updateItem(updateSpec)
    }

    fun getItems(): List<Recipe> {
        val table = dynamoDB.getTable(TABLE)
        val result = table.scan()

        return result.map {
            val recipe = Recipe(it.getString("title"),
                    it.getString("tags"),
                    it.getString("ingredients"),
                    it.getString("description"))
            recipe
        }
    }

    fun getTags(recipes: List<Recipe>): List<String> {
        return recipes.flatMap {
            it.tags.split(' ')
        }.toSet().toList().sorted()
    }

    fun getRecipeResponse(): GetRecipeResponse {
        val recipes = getItems()
        val tags = getTags(recipes)
        return GetRecipeResponse(recipes, tags)
    }
}