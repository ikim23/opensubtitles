module Main exposing (main)

import Browser
import Html exposing (Html, a, button, div, form, i, input, label, span, table, tbody, td, text, th, thead, tr)
import Html.Attributes exposing (checked, class, disabled, for, href, id, name, type_, value)
import Html.Events exposing (custom, onClick, onInput)
import Http
import Json.Decode exposing (Decoder, field, list, map, map4, string, succeed)



-- MAIN


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , subscriptions = subscriptions
        , view = view
        }



-- MODEL


type alias SubItem =
    { name : String
    , creationDate : String
    , downloadLink : String
    , downloadCount : String
    }


type alias Model =
    { userAgent : String
    , language : String
    , query : String
    , list : List SubItem
    }


init : () -> ( Model, Cmd Msg )
init _ =
    ( { userAgent = "TemporaryUserAgent"
      , language = "eng"
      , query = ""
      , list = []
      }
    , Cmd.none
    )



-- UPDATE


type Msg
    = UpdateUserAgent String
    | ToggleLanguage String
    | UpdateQuery String
    | Search
    | SearchDone (Result Http.Error (List SubItem))


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        UpdateUserAgent userAgent ->
            ( { model | userAgent = userAgent }, Cmd.none )

        ToggleLanguage lang ->
            ( { model | language = lang }, Cmd.none )

        UpdateQuery query ->
            ( { model | query = query }, Cmd.none )

        Search ->
            ( model, search model )

        SearchDone result ->
            case result of
                Ok list ->
                    ( { model | list = List.sortBy .downloadCount list }, Cmd.none )

                Err _ ->
                    ( model, Cmd.none )



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none



-- VIEW


view : Model -> Html Msg
view model =
    div [ class "container" ]
        [ div [ class "section" ]
            [ form [ custom "submit" (succeed { message = Search, preventDefault = True, stopPropagation = False }) ]
                [ div [ class "columns level" ]
                    [ div [ class "column is-2" ] [ label [ class "label", for "userAgent" ] [ text "User Agent:" ] ]
                    , div [ class "column is-10" ] [ input [ class "input", type_ "text", name "userAgent", value model.userAgent, onInput UpdateUserAgent ] [] ]
                    ]
                , div [ class "columns level" ]
                    [ div [ class "column is-2" ] [ label [ class "label" ] [ text "Languages:" ] ]
                    , div [ class "column is-10" ]
                        [ langCheckbox model "eng"
                        , langCheckbox model "svk"
                        , langCheckbox model "cze"
                        ]
                    ]
                , div [ class "columns level" ]
                    [ div [ class "column is-2" ] [ label [ class "label", for "query" ] [ text "Query:" ] ]
                    , div [ class "column is-8" ] [ input [ class "input", type_ "text", name "query", value model.query, onInput UpdateQuery ] [] ]
                    , div [ class "column is-2" ] [ button [ class "button is-fullwidth is-primary", type_ "submit", disabled (String.isEmpty model.query) ] [ text "Search" ] ]
                    ]
                ]
            ]
        , div [ class "section" ]
            [ table [ class "table is-fullwidth" ]
                [ thead []
                    [ tr []
                        [ th [] [ text "#" ]
                        , th [] [ text "Added" ]
                        , th [] [ text "Downloaded" ]
                        , th [] [ text "Name" ]
                        , th [] []
                        ]
                    ]
                , tbody []
                    (List.indexedMap
                        (\idx sub ->
                            tr []
                                [ td [] [ text (String.fromInt (idx + 1)) ]
                                , td [] [ text sub.creationDate ]
                                , td [] [ text sub.downloadCount ]
                                , td [] [ text sub.name ]
                                , td []
                                    [ a [ class "button", href sub.downloadLink ] [ text "Download" ]
                                    ]
                                ]
                        )
                        model.list
                    )
                ]
            ]
        ]


langCheckbox : Model -> String -> Html Msg
langCheckbox model lang =
    span [ class "mr-4" ]
        [ input [ type_ "radio", id lang, checked (model.language == lang), onClick (ToggleLanguage lang) ] []
        , label [ class "radio ml-1", for lang ] [ text (String.toUpper lang) ]
        ]



--- HTTP


search : Model -> Cmd Msg
search model =
    Http.request
        { method = "GET"
        , headers =
            [ Http.header "X-User-Agent" model.userAgent
            , Http.header "Accept" "application/json"
            ]
        , url = "https://rest.opensubtitles.org/search/query-" ++ model.query ++ "/sublanguageid-" ++ model.language
        , body = Http.emptyBody
        , expect = Http.expectJson SearchDone searchDecoder
        , timeout = Nothing
        , tracker = Nothing
        }


searchDecoder : Decoder (List SubItem)
searchDecoder =
    list subItemDecoder


subItemDecoder : Decoder SubItem
subItemDecoder =
    map4
        SubItem
        (field "SubFileName" string)
        dateDecoder
        (field "SubDownloadLink" string)
        (field "SubDownloadsCnt" string)


dateDecoder : Decoder String
dateDecoder =
    addDateDecoder |> Json.Decode.andThen dayDecoder


dayDecoder : String -> Decoder String
dayDecoder date =
    succeed (String.slice 0 10 date)


addDateDecoder : Decoder String
addDateDecoder =
    field "SubAddDate" string
