package com.serverless

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import io.moia.router.RequestHandler
import io.moia.router.Router.Companion.router
import io.moia.router.Request
import io.moia.router.ResponseEntity

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RemoveRecipe: RequestHandler() {
    private val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.EU_CENTRAL_1)
            .build()
    private val headers = mapOf("Access-Control-Allow-Origin" to "*", "Access-Control-Allow-Credentials" to "true")
    override val router = router {

        POST("delete-recipe") { r: Request<Recipe> ->
            RecipeService(client).deleteItem(r.body.title)
            ResponseEntity.ok(r.body, headers)
        }
        GET("delete-recipe") { r: Request<Unit> ->
            ResponseEntity.ok("OK", headers)
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(Handler::class.java)
    }
}


// curl -X GET https://wpghi57e1k.execute-api.eu-central-1.amazonaws.com/dev/delete-recipe
// curl -H "Content-Type: application/json" -X POST -d '{"title":"recipe2", "tags":"tag1", "ingredients":"in1 in2 in3","description":"describe" }'  https://wpghi57e1k.execute-api.eu-central-1.amazonaws.com/dev/delete-recipe
