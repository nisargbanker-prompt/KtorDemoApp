package com.example.routes

import com.example.data.model.Note
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


const val NOTES = "$API_VERSION/notes"
const val CREATE_REQUEST = "$NOTES/create"
const val UPDATE_REQUEST = "$NOTES/update"
const val DELETE_REQUEST = "$NOTES/delete"

@Location(CREATE_REQUEST)
class NoteCreateRoute

@Location(NOTES)
class NoteGetRoute

@Location(UPDATE_REQUEST)
class NoteUpdateRoute

@Location(DELETE_REQUEST)
class NoteDeleteRoute

fun Route.NoteRoutes(
    db: Repo,
    hashFunction: (String) -> String
) {

    authenticate("jwt") {
        post<NoteCreateRoute> {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note added successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem occurs"))
            }

        }

        get<NoteGetRoute> {

            try {
                val email = call.principal<User>()!!.email
                val notes = db.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Note>())
            }

        }

        post<NoteUpdateRoute> {

            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note updated successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem occurs"))
            }

        }

        delete<NoteDeleteRoute> {
            val noteId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "Query parameter id not present!!"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteNote(email, noteId)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note deleted successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem occurs"))
            }

        }


    }

}