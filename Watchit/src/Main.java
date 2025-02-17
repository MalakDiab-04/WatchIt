import model.*;
import model.user.Admin;
import model.user.Regular;
import model.user.User;
import service.*;
import java.lang.invoke.LambdaConversionException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static utils.Constants.*;
public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static String username;
    private static List<Movie> movies;
    private static List<Regular> users;
    private static List<Admin> admins;
    private static List<Actor> actors;
    private static List<Director> directors;

    public static void main(String[] args) {
        readDataFromFiles();
        RatingService.setRatingOfWatchItMovies(movies);
        Playlist.setTopWatchedMovies(movies);
        System.out.println("\t\t\t\t\t\tWelcome to WATCH IT\n\n");
        boolean continueLoop = true;
        boolean loggedInAsAnAdmin = false;
        User loggedInUser = null;
        while (continueLoop) {
            System.out.println("Press: \n 1:To register\n 2:To Login");
            int userResponse = input.nextInt();
            switch (userResponse) {




                case 1: {
                    registerAnAccount(users);
                    break;
                }
                case 2: {
                    loggedInUser = logIN(users, admins);
                    loggedInAsAnAdmin = (loggedInUser instanceof Admin);
                    continueLoop = false;
                    break;
                }
                default: {
                    System.out.println("Invalid option. Please try again.");
                    break;
                }
            }
        }
        if (loggedInAsAnAdmin) {
            char response;
            do {
                System.out.println("Press: \n1:To add a movie\n2:To edit a movie\n3:To delete a movie\n4:To display the most subscribed plan\n5:To display the month with most revenue");
                int adminResponse = input.nextInt();
                switch (adminResponse) {
                    case 1:
                        addMovie(movies);
                        break;
                    case 2:
                        editMovies(movies);
                        break;
                    case 3:
                        deleteMovies(movies);
                        break;
                    case 4:
                        displayMostSubscribed(users);
                        break;
                    case 5:
                        displayMonthWithMostRevenue(users);
                        break;
                }
                System.out.println("Need any more service?" + "... Press Y to continue and N to exit");
                response = input.next().charAt(0);
            } while (response == 'y' || response == 'Y');
        } else {

            checkSubscription(users);
            char response;
            do {
                System.out.println("Press: \n1:To search for a movie\n2:To search for actors\n3:To search for directors\n4:To display your movie lists (favourites,watch later)\n5:To display your watched movie records\n6:To watch a movie\n7:To display your recent watched movies\n8:To display top rated movies\n9:To display your top watched movies\n10:To edit your profile information \n11:To delete your account\n12:To exit");

                int userInput = input.nextInt();
                switch (userInput) {
                    case 1:
                        searchForMovie(movies, users);
                        break;
                    case 2:
                        searchForActors(actors);
                        break;
                    case 3:
                        searchForDirectors(directors);
                        break;
                    case 4:
                        displayLists((Regular) loggedInUser);
                        break;
                    case 5:
                        displayWatchRecord(users);
                        break;
                    case 6:
                        displayMoviesAndWatch(movies, users);
                        break;
                    case 7:
                        displayRecentWatched(users);
                        break;
                    case 8:
                        displayTopRated(movies);
                        break;
                    case 9:
                        displayTopWatched();
                        break;
                    case 10:
                        UserEditInfo(users);
                        break;
                    case 11:
                        deleteUserAccount(users);
                        break;
                    case 12:
                        RegularService.writeUsersToFile(users);
                        System.exit(0);
                }

                System.out.println("Need any more service?" + "... Press Y to continue and N to exit");
                response = input.next().charAt(0);
            } while (response == 'y' || response == 'Y');
        }
        writeDataInFiles(admins, users, movies);
    }

    private static void registerAnAccount(List<Regular> users) {
        System.out.println("Please enter the following details to complete your registration");

        System.out.println("Enter your id: (last 4 digits only): ");
        String ID = input.next();

        while (!isIdValid(ID)) {
            input.nextLine();
            System.out.println("Invalid ID..Please enter exactly 4 digits");
            ID = input.next();
        }
        input.nextLine();
        System.out.print("Enter username: ");
        username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        System.out.print("Enter first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter last name: ");
        String lastName = input.nextLine();

        System.out.print("Enter email: ");
        String email = input.nextLine();
        while (!email.contains("@gmail.com") && !email.contains("@yahoo.com") && !email.contains("@cis.asu.edu.eg")) {
            System.out.println("Kindly ensure that your email address includes the '@' symbol followed by the domain name dot com when composing your message. Thank you.");
            System.out.print("Re-enter email: ");
            email = input.nextLine();
        }
        System.out.println("Account created successfully");
        Subscriptions newRegisteredSubscription = new Subscriptions(false, null, null);
        Regular newUser = new Regular(ID, username, password, firstName, lastName, email, null, null, newRegisteredSubscription, 0);
        AdminService.addRegularUsers(users, newUser);
    }

    private static User logIN(List<Regular> users, List<Admin> admins) {
        System.out.println("Please enter your username:");
        username = input.next();
        input.nextLine();

        System.out.println("Please enter your password:");
        String password = input.nextLine();

        while (true) {
            for (Regular userx : users) {
                if (username.equals(userx.getUserName()) && password.equals(userx.getPassword())) {
                    return userx;
                }
            }
            for (Admin admin : admins) {
                if (username.equals(admin.getUserName()) && password.equals(admin.getPassword())) {
                    return admin;
                }
            }

            System.out.println("Incorrect username or password... Please re-enter correct username and password:");

            System.out.println("Please enter your username:");
            username = input.next();

            System.out.println("Please enter your password:");
            password = input.next();
        }


    }

    private static void enterSubscription(List<Regular> users, int index) {
        System.out.println("\nBasic Plan: \t Standard Plan: \t Premium Plan: \t ");
        System.out.println("\nPrice: " + BASIC_PRICE + "LE  \t Price: " + STANDARD_PRICE + "LE  \t Price: " + PREMIUM_PRICE);
        System.out.println("\nYou have up to: " + BASIC_MOVIES + " movies per month \t You have up to: " + STANDARD_MOVIES + " movies per month \t You have up to: " + PREMIUM_MOVIES + " movies per month");
        System.out.println("\nWhich plan do you want to subscribe to?");
        String enteredPlan = input.next().toLowerCase();
        while (!enteredPlan.equals("basic") && !enteredPlan.equals("standard") && !enteredPlan.equals("premium")) {
            System.out.println("Invalid plan..Re-enter the desired plan");
            enteredPlan = input.next().toLowerCase();
        }
        AdminService.addSubscription(users, enteredPlan, index);
    }

    private static int findUserIndexByUsername(List<Regular> users) {
        for (int i = 0; i < users.size(); i++) {
            if (username.contains(users.get(i).getUserName())) {
                return i;
            }
        }
        return -1;
    }

    private static void checkSubscription(List<Regular> users) {
        int index = findUserIndexByUsername(users);
        if (index == -1) {
            System.err.println("User not found");
            System.exit(0);   //exits the method at this point
        }
        Subscriptions Subscription = users.get(index).getSubscription();
        if (!Subscription.isStatus()) {
            System.out.println("Subscribe to one of our plans");
            enterSubscription(users, index);
        } else {
            if (!Subscriptions.dueDate(Subscription.getSubscribeDate())) {
                System.out.println("You're subscribed to the " + users.get(index).getSubscription().getPlan() + " plan!");
            } else {
                System.out.println("Your subscription has ended");
                Subscriptions sub = new Subscriptions(false, null, null);
                users.get(index).setSubscription(sub);
                System.out.println("Re-subscribe to one of our plans");
                System.out.println("Press:\n1:To subscribe\n2:To exit");
                int choose = input.nextInt();
                switch (choose) {
                    case 1:
                        enterSubscription(users, index);
                        break;
                    case 2:
                        System.exit(0);
                        break;
                }
            }
        }

    }

    private static void displayMoviesAndWatch(List<Movie> movies, List<Regular> users) {
        int index = findUserIndexByUsername(users);
        if (index == -1) {
            System.err.println("User not found");
            System.exit(0);
        }
        System.out.println("Those are our available movies for you to watch:");
        for (Movie movie : movies) {
            System.out.println(movie.getMovieTitle());
        }
        System.out.println("\nPress:\n1:To add any of them to your watch later playlist\n2:To currently watch one");
        int decision = input.nextInt();
        switch (decision) {
            case 1: {
                boolean isFound = false;
                System.out.println("Enter the movie name you want to add");
                input.nextLine();
                String movieName = input.nextLine();
                for (Movie movie : movies) {
                    if (movieName.equalsIgnoreCase(movie.getMovieTitle())) {
                        isFound = true;
                        Playlist playlist = users.get(index).getPlayLists();
                        if (playlist != null) {
                            users.get(index).getPlayLists().addToToBeWatched(movie.getMovieTitle());
                            System.out.println("Successfully added to you watch later playlist!");
                        } else {
                            playlist = new Playlist();
                            users.get(index).setPlayLists(playlist);
                            users.get(index).getPlayLists().addToToBeWatched(movie.getMovieTitle());
                            System.out.println("Successfully added to you watch later playlist!");
                        }
                        break;
                    }
                }
                if (!isFound) System.err.println("Movie not found\n");
                break;
            }
            case 2: {
                if (users.get(index).getNumberOfMoviesWatched() > checkRequiredCounter(users, index)) {
                    System.out.println("You've exceeded the number of movies available to watch this month");
                } else {
                    int movieIndex = 0;//3shan aayzin ne3raf ehna f anhy index movie
                    System.out.println("\nWrite the movie name you want to watch:");
                    input.nextLine();
                    String response = input.nextLine();
                    boolean isFound = false;
                    for (Movie movie : movies) {
                        if (response.equalsIgnoreCase(movie.getMovieTitle())) {
                            isFound = true;
                            Float rating;
                            Playlist playlist = users.get(index).getPlayLists();
                            if (playlist == null) {
                                // If the playlist is null, create a new playlist and set it to the user
                                playlist = new Playlist();
                                users.get(index).setPlayLists(playlist);
                            }
                            playlist.getAndAddRecentWatchedMovies(movie.getMovieTitle());
                            System.out.println("Movie watched successfully!" + "\nDid you enjoy the movie?" + "\nPlease enter a movie rating from 1-5..Enter 0 if you don't want to rate");
                            int newCounter = users.get(index).getNumberOfMoviesWatched() + 1;
                            users.get(index).setNumberOfMoviesWatched(newCounter);
                            while (true) {
                                rating = input.nextFloat();
                                if (rating == 0) {
                                    rating = null;
                                    break;
                                }
                                input.nextLine();
                                if (rating >= 1.0f && rating <= 5.0f) {
                                    break;
                                }
                                System.out.println("Invalid entry...Please enter a rating between 1 and 5");
                            }
                            WatchRecord w_record = users.get(index).getWatchrecord();
                            if (users.get(index).getWatchrecord() == null) {
                                w_record = new WatchRecord(users.get(index).getID()); //akhaly yesawr ala constructor fady fa watchrecord ybaa null
                            }
                            w_record.addToWatched(movie.getMovieTitle(), LocalDate.now(), rating, users.get(index).getID());
                            users.get(index).setWatchrecord(w_record);
                            if (rating != null) RatingService.CalculateRating(movies, movie.getMovieTitle(), rating);
                            Playlist.getAndAddTopWatchedMovies(movieIndex, movies);
                            System.out.println("Do you want to add it to your favorite playlist?... Press Y for yes and N for no");
                            String answer = input.next();
                            if ((answer.equalsIgnoreCase("Y"))) {
                                users.get(index).getPlayLists().addToFavorite(movie.getMovieTitle());
                                System.out.println("Successfully added to your favorite playlist!");
                            }
                            break;
                        }
                        movieIndex++;
                    }
                    if (!isFound) System.err.println("Movie not found\n");
                    break;
                }
            }
        }
    }

    private static void addMovie(List<Movie> movies) {
        Movie newMovie = null;
        try {
            System.out.println("Enter the details of the movie:");

            System.out.println("MovieId:");
            int movieId = input.nextInt();
            input.nextLine(); // 3ashan threw error bsabb nextint()

            System.out.println("Movie name:");
            String movieName = input.nextLine();

            System.out.println("Date of release(yyyy-mm-dd):");
            String movieDateString = input.nextLine();
            LocalDate releaseDate = LocalDate.parse(movieDateString);

            System.out.println("Movie Duration(hh:mm)  :");
            LocalTime durationTime = LocalTime.parse(input.nextLine());

            System.out.println("Movie Actors: (Write 'done' when finished):");
            ArrayList<String> actors = new ArrayList<>();
            while (true) {
                String actorName = input.nextLine();
                if (actorName.equalsIgnoreCase("done")) {
                    break;
                }
                actors.add(actorName);
            }

            System.out.println("Movie Director:");
            String director = input.nextLine();

            System.out.println("Movie Genres (Write 'done' when finished):");
            ArrayList<String> genres = new ArrayList<>();
            while (true) {
                String genre = input.nextLine();
                if (genre.equalsIgnoreCase("done")) {
                    break;
                }
                genres.add(genre);
            }

            System.out.println("Origin country:");
            String country = input.nextLine();

            System.out.println("Movie Budget:");
            float budget = input.nextFloat();

            System.out.println("Movie Revenue:");
            float revenue = input.nextFloat();

            System.out.println("Movie IMDb_Score:");
            float imdbScore = input.nextFloat();

            input.nextLine(); // Consume the newline character

            System.out.println("Movie Languages (Write 'done' when finished):");
            ArrayList<String> languages = new ArrayList<>();
            while (true) {
                String language = input.nextLine();
                if (language.equalsIgnoreCase("done")) {
                    break;
                }
                languages.add(language);
            }

            System.out.println("Movie Poster Path:");
            String poster = input.nextLine();
            newMovie = new Movie(movieId, movieName, releaseDate, durationTime, actors, director, genres, country, budget, revenue, imdbScore, languages, poster);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (newMovie != null)
            movies.add(newMovie);
    }
    private static ArrayList<String> getActorsFromUser() {

        System.out.println("Enter the new list of actors (separate each actor with a semicolon):");
        String actorsInput = input.nextLine();

        //  Arrays.asList to convert the array returned by split to a List
        return new ArrayList<>(Arrays.asList(actorsInput.split(";")));
    }

    private static ArrayList<String> getGenresFromUser() {

        System.out.println("Enter the new list of genres (separate each actor with a semicolon):");
        String genresInput = input.nextLine();

        //  Arrays.asList to convert the array returned by split to a List
        return new ArrayList<>(Arrays.asList(genresInput.split(";")));
    }

    private static ArrayList<String> getLanguagesFromUser() {
        System.out.println("Enter the new list of languages (separate each actor with a semicolon):");
        String languagesInput = input.nextLine();
        //  Arrays.asList to convert the array returned by split to a List
        return new ArrayList<>(Arrays.asList(languagesInput.split(";")));
    }

    private static void editMovies(List<Movie> movies) {
        //how to make sure in el movie da mawgood aandna
       boolean isFound = false;
        input.nextLine();
        System.out.println("Enter the movie name you want to edit:");
        String movieTitle = input.nextLine();
        System.out.println("Press: \n1 to edit movie duration \n2 to edit movie release date \n3 to edit movie actors \n4 to edit movie director \n5 to edit movie genres \n6 to edit movie origin country \n7 to edit movie budget \n8 to edit movie revenue \n9 to edit movie imdb_score \n10 to edit movie languages \n11 to edit movie poster path ");
        int userInput = input.nextInt();
        for (Movie mov : movies) {
            if (mov.getMovieTitle().equalsIgnoreCase(movieTitle)) {
                isFound = true;
                switch (userInput) {
                    case 1:
                        input.nextLine();
                        System.out.println("Enter updated time duration(hh:mm):");
                        String newTime = input.nextLine();
                        mov.setDurationTime(LocalTime.parse(newTime));
                        System.out.println("Movie time duration updated successfully");
                        break;
                    case 2:
                        input.nextLine();
                        System.out.println("Enter updated Date(yyyy-mm-dd):");
                        String newDate = input.nextLine();
                        mov.setReleaseDate(LocalDate.parse(newDate));
                        System.out.println("Movie release date updated successfully");
                        break;
                    case 3: //edit an actor
                        System.out.println("Press:\n 1: To add an actor \n 2:To remove an actor \n 3:Change the whole list ");
                        int respone = input.nextInt();

                        if (respone == 1) {
                            input.nextLine();
                            System.out.println("Enter new actor name:");
                            String newActor = input.nextLine();
                            mov.getActors().add(newActor);
                            System.out.println("Actor added successfully");
                        }
                        if (respone == 2) {
                            input.nextLine();
                            System.out.println("Enter the actor desired to be deleted:");
                            String actorToBeDeleted = input.nextLine();
                            int index = -1;
                            for (int i = 0; i < mov.getActors().size(); i++) {
                                if (mov.getActors().get(i).equalsIgnoreCase(actorToBeDeleted)) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index >= 0) {
                                mov.getActors().remove(index);
                                System.out.println("Actor removed successfully.");
                            } else {
                                System.err.println("Actor not isFound in the list.");
                            }

                        }
                        if (respone == 3) {
                            input.nextLine();
                            mov.setActors(getActorsFromUser());
                            System.out.println("Actor list substituted successfully");
                        }
                        break;
                    case 4:
                        input.nextLine();
                        System.out.println("Enter the new director:");
                        String newDirector = input.nextLine();
                        mov.setDirector(newDirector);
                        System.out.println("Director added successfully");
                        break;
                    case 5:
                        System.out.println("Press:\n 1: To add a genre \n 2:To remove a genre \n 3:Change the entire list of genres ");
                        int choice = input.nextInt();

                        if (choice == 1) {
                            input.nextLine();
                            System.out.println("Enter new genre:");
                            String newGenre = input.nextLine();
                            mov.getGenres().add(newGenre);
                            System.out.println("Genre added successfully");
                        }
                        if (choice == 2) {
                            input.nextLine();
                            System.out.println("Enter the genre desired to be deleted:");
                            String genreToBeDeleted = input.nextLine().toLowerCase();
                            int index = -1;
                            for (int i = 0; i < mov.getGenres().size(); i++) {
                                if (mov.getGenres().get(i).equalsIgnoreCase(genreToBeDeleted)) {
                                    index = i;
                                    break;
                                }
                            }

                            if (index >= 0) {

                                mov.getGenres().remove(index);
                                System.out.println("Genre removed successfully.");
                            } else {
                                System.err.println("Genre not isFound in the list.");
                            }
                        }
                        if (choice == 3) {
                            input.nextLine();
                            mov.setGenres(getGenresFromUser());
                            System.out.println("Genre list substituted successfully");
                        }
                        break;
                    case 6:
                        input.nextLine();
                        System.out.println("Enter the new country:");
                        String newCountry = input.nextLine();
                        mov.setCountry(newCountry);
                        System.out.println("Movie origin country updated successfully");
                        break;
                    case 7:
                        input.nextLine();
                        System.out.println("Enter the updated Budget:");
                        String newBudget = input.nextLine();
                        mov.setBudget(Float.parseFloat(newBudget));
                        System.out.println("Movie budget updated successfully");
                        break;
                    case 8:
                        input.nextLine();
                        System.out.println("Enter updated Revenue:");
                        String newRevenue = input.nextLine();
                        mov.setRevenue(Float.parseFloat(newRevenue));
                        System.out.println("Movie revenue updated successfully");
                        break;
                    case 9:
                        input.nextLine();
                        System.out.println("Enter updated Imdb_score:");
                        String newScore = input.nextLine();
                        mov.setImdb_score(Float.parseFloat(newScore));
                        System.out.println("Movie Imdb_score updated successfully");
                        break;

                    case 10:
                        System.out.println("Press:\n 1: To add a language \n 2:To remove a language \n 3:Change the whole list of languages ");
                        int answer = input.nextInt();
                        if (answer == 1) {
                            input.nextLine();
                            System.out.println("Enter new language:");
                            String newLanguage = input.nextLine();
                            mov.getLanguages().add(newLanguage);
                            System.out.println("Language added successfully");
                        }
                        if (answer == 2) {
                            input.nextLine();
                            System.out.println("Enter the language desired to be deleted:");
                            String languageToBeDeleted = input.nextLine();
                            int index = -1;
                            for (int i = 0; i < mov.getLanguages().size(); i++) {
                                if (mov.getLanguages().get(i).equalsIgnoreCase(languageToBeDeleted)) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index >= 0) {
                                mov.getLanguages().remove(index);
                                System.out.println("language removed successfully.");
                            } else {
                                System.err.println("language not isFound in the list.");
                            }

                        }
                        if (answer == 3) {
                            input.nextLine();
                            mov.setLanguages(getLanguagesFromUser());
                            System.out.println("Languages list substituted successfully");
                        }
                        break;

                    case 11:
                        input.nextLine();
                        System.out.println("Enter new movie poster path:");
                        String newPoster = input.nextLine();
                        mov.setPoster(newPoster);
                        System.out.println("Movie poster path updated successfully");
                        break;

                }
            }
        }
        if (!isFound) {
            System.err.println("Movie not isFound");
        }
    }

    private static void deleteMovies(List<Movie> movies) {
        input.nextLine();
        System.out.println("Enter the movie name or list of movies you want to remove (separate movies with a semicolon if more than one ):");
        String moviesToDelete = input.nextLine();
        List<String> moviesToBeDeleted = Arrays.asList(moviesToDelete.split(";"));
        boolean removed = movies.removeIf(movie -> moviesToBeDeleted.contains(movie.getMovieTitle()));
        if (removed) {
            System.out.println("Movies with the specified titles removed successfully.");
        } else {
            System.out.println("No movies found with the specified titles.");
        }
    }

    private static boolean isIdValid(String id) {
        return id.matches("\\d{4}");
    }

    private static void checkEnteredId(String savedId) {
        while (!isIdValid(savedId)) {
            input.nextLine();
            System.err.println("Invalid ID..Please enter exactly 4 digits");
            savedId = input.next();
        }
    }

    private static void UserEditInfo(List<Regular> users) {
        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            if (username.equals(users.get(i).getUserName())) {
                index = i;
                break;
            }
        }
        boolean isFound = false;
        System.out.println("Enter your saved id: (last 4 digits only): ");
        String savedId = input.next();
        while (true) {
            if (!users.get(index).getID().equals(savedId)) {
                checkEnteredId(savedId);
                input.nextLine();
                System.err.println("User Not Found..Re enter: ");
                savedId = input.next();

            } else {
                break;
            }
        }

        System.out.println("Press: \n1 to edit your id (write only last 4 numbers) \n2 to edit your username \n3 to edit your password \n4 to edit your first name \n5 to edit your last name \n6 to edit your email \n7 to change your subscription plan ");
        int choice = input.nextInt();
        switch (choice) {
            case 1:
                System.out.println("Enter updated id (last 4 digits only):");
                String newId = input.next();
                while (!isIdValid(newId)) {
                    input.nextLine();
                    System.out.println("Invalid ID..Please enter exactly 4 digits");
                    newId = input.next();
                }
                AdminService.AdminEditUsers(savedId, users, 1, newId);
                break;
            case 2:
                input.nextLine();
                System.out.println("Enter updated username:");
                String newUsername = input.nextLine();
                AdminService.AdminEditUsers(savedId, users, 2, newUsername);
                break;
            case 3:
                input.nextLine();
                System.out.println("Enter updated password:");
                String newPassword = input.nextLine();
                AdminService.AdminEditUsers(savedId, users, 3, newPassword);
                break;
            case 4:
                input.nextLine();
                System.out.println("Enter first name after update:");
                String newFirstName = input.nextLine();
                AdminService.AdminEditUsers(savedId, users, 4, newFirstName);
                break;
            case 5:
                input.nextLine();
                System.out.println("Enter last name after update:");
                String newLastName = input.nextLine();
                AdminService.AdminEditUsers(savedId, users, 5, newLastName);
                break;
            case 6:
                input.nextLine();
                System.out.println("Enter updated email:");
                String newEmail = input.nextLine();
                AdminService.AdminEditUsers(savedId, users, 6, newEmail);
                break;
            case 7:
                input.nextLine();
                System.out.println("Press: \n1 to unsubscribe \n2 to change plan ");
                int ans = input.nextInt();
                while ((ans != 1) && (ans != 2)) {
                    System.out.println("Invalid choice ..Please enter either 1 or 2 only ");
                    ans = input.nextInt();
                }
                if (ans == 1) {
                    AdminService.AdminEditUsers(savedId, users, 7, "false");
                } else {
                    System.out.println("What plan do you want to upgrade to (Basic/Standard/Premium)");
                    String newPlan = input.next();
                    AdminService.AdminEditUsers(savedId, users, 8, newPlan.toLowerCase());
                }
                break;
        }
    }

    private static void deleteUserAccount(List<Regular> users) {
        input.nextLine();
        boolean found = false;
        System.out.println("To confirm deleting your account enter your password");
        String response = input.nextLine();
        for (int index = 0; index < users.size(); index++) {
            Regular user = users.get(index);
            if (response.equals(user.getPassword())) {
                AdminService.adminRemovesUserAccount(users, index);
                found = true;
                break;
            }
        }
        if (!found) System.out.println("User not found");
    }

    private static void searchForActors(List<Actor> actors) {
        boolean isFound = false;
        System.out.println("Enter the actor's name:");
        input.nextLine();
        String actorName = input.nextLine();
        for (int index = 0; index < actors.size(); index++) {
            Actor actor = actors.get(index);
            if (actorName.equalsIgnoreCase(actor.getFullName())) {
                CastService.displayActorDetails(actors, index);
                isFound = true;
            }
        }
        if (!isFound) System.err.println("Actor not found");
    }

    private static void searchForDirectors(List<Director> directors) {
        boolean isFound = false;
        System.out.println("Enter the director's name:");
        input.nextLine();
        String directorName = input.nextLine();
        for (int index = 0; index < directors.size(); index++) {
            Director director = directors.get(index);
            if (directorName.equalsIgnoreCase(director.getFullName())) {
                CastService.displayDirectorDetails(directors, index);
                isFound = true;
            }
        }
        if (!isFound) System.err.println("Director not found");
    }

    private static int checkRequiredCounter(List<Regular> users, int index) {
        if (users.get(index).getSubscription().getPlan().equalsIgnoreCase("basic"))
            return BASIC_MOVIES;
        else if (users.get(index).getSubscription().getPlan().equalsIgnoreCase("standard"))
            return STANDARD_MOVIES;
        else if (users.get(index).getSubscription().getPlan().equalsIgnoreCase("premium"))
            return PREMIUM_MOVIES;
        return 0; //todo recheck
    }

    private static void searchForMovie(List<Movie> movies, List<Regular> users) {
        System.out.println("Press: \n1 to search for a specific movie by its name \n2 to search for movies by a specific genre");
        int in = input.nextInt();
        int index = findUserIndexByUsername(users);
        if (index == -1) {
            input.nextLine();
            System.err.println("User not found");
            System.exit(0);   //exits the method at this point
        }
        input.nextLine();
        switch (in) {
            case 1:
                System.out.println("Enter Movie name to search for:");
                String movieName = input.nextLine();
                Movie MovieReturned = MovieService.searchForMovieByTitle(movies, movieName);
                if (MovieReturned == null) System.err.println("This movie isn't available");
                else {
                    int movieIndex = -1;
                    for (int j = 0; j < movies.size(); j++) {
                        if (MovieReturned.getMovieId() == movies.get(j).getMovieId()) {
                            movieIndex = j; //get index of movie
                            break;
                        }
                    }
                    System.out.println("Movie details: \nDuration: " + MovieReturned.getDurationTime() + "\nImdb_score: " + MovieReturned.getImdb_score() + "\nOrigin country: " + MovieReturned.getCountry() + "\nActors: " + MovieReturned.getActors() + "\nDirector: " + MovieReturned.getDirector() + "\nGenres: " + MovieReturned.getGenres() + "\nLanguaes: " + MovieReturned.getLanguages() + "\nRelease Year: " + MovieReturned.getReleaseDate().getYear());
                    System.out.println("Do you want to watch this movie?.....Enter yes to watch");
                    String response = input.next();
                    if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")) {

                        if (users.get(index).getNumberOfMoviesWatched() > checkRequiredCounter(users, index)) {
                            System.out.println("You've exceeded the number of movies available to watch this month");
                        } else {
                            Playlist playlist = users.get(index).getPlayLists();
                            if (playlist == null) {
                                // If the playlist is null, create a new playlist and set it to the user
                                playlist = new Playlist();
                                users.get(index).setPlayLists(playlist);
                            }
                            playlist.getAndAddRecentWatchedMovies(MovieReturned.getMovieTitle());
                            System.out.println("Movie watched successfully!" + "\nDid you enjoy the movie?" + "\nPlease enter a movie rating from 1-5..Enter 0 if you don't want to rate");
                            int newCounter = users.get(index).getNumberOfMoviesWatched() + 1;
                            users.get(index).setNumberOfMoviesWatched(newCounter);
                            Float rating;
                            while (true) {
                                rating = input.nextFloat();
                                if (rating == 0) {
                                    rating = null;
                                    break;
                                }
                                if (rating >= 1.0f && rating <= 5.0f) {
                                    break;
                                }
                                System.out.println("Invalid entry...Please enter a rating between 1 and 5");
                            }
                            WatchRecord w_record = users.get(index).getWatchrecord();
                            if (users.get(index).getWatchrecord() == null) {
                                w_record = new WatchRecord(users.get(index).getID()); //akhaly yesawr ala constructor fady fa watchrecord ybaa null
                            }
                            w_record.addToWatched(MovieReturned.getMovieTitle(), LocalDate.now(), rating, users.get(index).getID());
                            users.get(index).setWatchrecord(w_record);
                            input.nextLine();
                            if (rating != null)
                                RatingService.CalculateRating(movies, MovieReturned.getMovieTitle(), rating);
                            Playlist.getAndAddTopWatchedMovies(movieIndex, movies);
                            System.out.println("Do you want to add it to your favorite playlist?... Press Y for yes and N for no");
                            String answer = input.nextLine();
                            if ((answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("yes"))) {
                                users.get(index).getPlayLists().addToFavorite(MovieReturned.getMovieTitle());
                            }
                        }
                    }
                }
                break;
            case 2:
                System.out.println("Enter Genre to search by:");
                String Genre = input.next();
                List<Movie> MoviesFound = MovieService.searchForMovieByGenre(movies, Genre);
                int i = 1;
                if (MoviesFound.isEmpty()) System.err.println("\nMovies with that genre are not found");
                else {
                    for (Movie movie : MoviesFound) {
                        System.out.println("Movie " + i + ": \nMovie Name:" + movie.getMovieTitle() + " \nMovie duration: " + movie.getDurationTime() + "\nImdb_score: " + movie.getImdb_score() + "\nOrigin country: " + movie.getCountry() + "\nActors: " + movie.getActors() + "\nDirector: " + movie.getDirector() + "\nLanguaes: " + movie.getLanguages() + "\nRelease Year: " + movie.getReleaseDate().getYear() + "\nGenres:" + movie.getGenres() + "\n");
                        i++;
                    }
                }
                break;
        }
    }
    private static void displayMostSubscribed(List<Regular> users) {
        System.out.println("The most subscribed plan up till now is the " + AdminService.seeMostSubscribed(users) + " plan");
    }

    private static void displayLists(Regular user) {
        System.out.println("Press: \n1:To display favourites list \n2:To display watchLater list");
        int entered = input.nextInt();
        input.nextLine();
        Playlist Playlists = user.getPlayLists(); // Assuming getPlayLists() cannot return null
        if (Playlists != null) {
            switch (entered) {
                case 1:
                    List<String> favoritePlaylist = Playlists.getFavoritePlaylist();
                    if (favoritePlaylist == null || favoritePlaylist.isEmpty()) {
                        System.out.println("Your favorite playlist is currently empty. Add some favorite movies to create your personalized playlist!");
                    } else {
                        System.out.println(favoritePlaylist);
                    }
                    break;
                case 2:
                    List<String> watchLaterPlaylist = Playlists.getWatchLaterplaylist();
                    if (watchLaterPlaylist == null || watchLaterPlaylist.isEmpty()) {
                        System.out.println("Your Watchlater playlist is currently empty. Add some movies to create your personalized playlist!");
                    } else {
                        System.out.println(watchLaterPlaylist);
                    }
                    break;
            }
        } else {
            System.err.println("Playlist not available for the user (Not found) ");
        }
    }

    private static void displayWatchRecord(List<Regular> users) {
        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            if (username.equals(users.get(i).getUserName())) {
                index = i;
                break;
            }
        }
        WatchRecord checkRecord = users.get(index).getWatchrecord();
        if (checkRecord == null) System.err.println("You haven't watched anything yet"); //law new register
        else {
            System.out.println("Watched record details:\n\nName\t \t Date Of Watching\t \t Rate given");
            String userId = users.get(index).getID();
            for (MovieRecord record : users.get(index).getWatchrecord().getWatchedRecord()) {
                if (record.getUserId().equals(userId)) {
                    if (record.getRating() == null)
                        System.out.println(record.getMovieName() + "\t \t" + record.getWatchDate() + "\t \t" + "No rating was given");
                    else
                        System.out.println(record.getMovieName() + "\t \t" + record.getWatchDate() + "\t \t" + record.getRating());
                }
            }
        }

    }

    private static void displayMonthWithMostRevenue(List<Regular> users) {
        for (int i = 0; i < users.size(); i++) {
            Integer monthValue = null;
            LocalDate date = users.get(i).getSubscription().getSubscribeDate();
            if (date != null) {
                monthValue = date.getMonthValue();
                AdminService.calculateRevenue(users, monthValue, i);
            }

        }
        int[] monthRevenues = AdminService.getMonthRevenues();
        int maxIndex = 0;
        for (int i = 0; i < 12; i++) {
            if (monthRevenues[i] > monthRevenues[maxIndex]) {
                maxIndex = i;
            }
        }
        maxIndex++;
        System.out.println("The month that had the most revenue is month " + maxIndex);
    }

    private static void displayRecentWatched(List<Regular> users) {
        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            if (username.contains(users.get(i).getUserName())) {
                index = i;
                break;
            }
        }

        Playlist playlist = users.get(index).getPlayLists();
        if (playlist == null) {
            // If the playlist is null, create a new playlist and set it to the user
            playlist = new Playlist();
            users.get(index).setPlayLists(playlist);
        }
        if (Arrays.toString(playlist.getRecentMovies()).equals("[null, null, null]")) {
            System.err.println("You haven't watched any movies");
        } else {
            String[] RecentMovies = playlist.getRecentMovies();
            System.out.println("Your recently watched movies are: ");
            for (int i = 2; i >= 0; i--) {
                if (RecentMovies[i] != null)
                    System.out.println(RecentMovies[i]); //beyrag3 address el array msh el object l gowah bzbt
            }
        }
    }
    private static void displayTopRated(List<Movie> movies) {
        String[] topRatedMovies = Playlist.getTopRatedMovies();
        Playlist.getAndAddTopRatedMovies(movies);
        System.out.println("The top rated movies are:\n");
        for (int i = 0; i < 3; i++) {
            System.out.println(i + 1 + "." + topRatedMovies[i] + "\n");
        }
    }

    private static void displayTopWatched() {
        String[] topWatchedMovies = Playlist.getTopWatchedMovies();
        boolean allNull = true;
        for (String element : topWatchedMovies) {
            if (element != null) {
                allNull = false;
                break;
            }
        }
        if (allNull) {
            System.err.println("You haven't watched anything yet");
        } else {
            System.out.println("Your top watched movies are:\n");
            for (int i = 0; i < 3; i++) {
                if (topWatchedMovies[i] != null) {
                    System.out.println(topWatchedMovies[i] + "\n");
                }
            }
        }
    }

    public static void readDataFromFiles() {
        movies = MovieService.readMoviesFromFile();
        users = RegularService.readUsersFromFile();
        admins = AdminService.readAdminsFromFile();
        actors = CastService.readActorsFromFile();
        directors = CastService.readDirectorFromFile();
    }
    private static void writeDataInFiles(List<Admin> admins, List<Regular> users, List<Movie> movies) {
        AdminService.writeAdminsToFile(admins);
        RegularService.writeUsersToFile(users);
        MovieService.writeMoviesToFile(movies);
    }
}
