#= 
juliacaller:
- Julia version: 1.4
- Author: Mehmet Hakan Satman
- Date: 2020-04-03
=#

using Pkg, Sockets


a = try
    	v = Pkg.installed()["JSON"]
	catch
    	Pkg.add("JSON")
end


using JSON

function writeln(client, str)
	write(client, str)
	write(client, "\r\n")
end

"""
Handles the client connection.

Command list:
	execute: Evaluates the given statement or expression, possible multiple items seperated with semicolon.

	get: Returns the value of a given variable in JSON format.

	exit: Closes the client connection.

	shutdown: Closes the server.


Examples:

	execute a = 99

	get a

	(Result is {"a":99})


	execute using Distributions

	execute norm = Normal(0, 1)

	execute numbers = rand(norm, 5)

	get numbers

	(Result is {"numbers":[-1.1734648123994915,-0.5577848419508594,0.5628431386136749,0.42211832659070825,-0.4402667291521316]})
"""
function handle_client(server, client)
	while true
		__line__ = readline(client)
		println(__line__)
			if startswith(__line__, "execute ")
				__command__ = __line__[9:end]
				try
					@info __command__
					eval(Meta.parse(__command__))
				catch mth_err
					@error mth_err
					close(client)
					close(server)
				end
				# writeln(client, "eval okay")
			elseif startswith(__line__, "get ")
				__varname__ = __line__[5:end]
				__D__ = Dict(__varname__ => eval(Meta.parse(__varname__)))
				writeln(client, json(__D__))
			elseif startswith(__line__, "exit")
				break
			elseif startswith(__line__, "install ")
				__pkg__ = __line__[9:end]
				Pkg.add(__Pkg__)
			elseif startswith(__line__, "shutdown")
				close(client)
				close(server)
				break
			end
	end
	close(client)
end


"""
Creates a TCP server socket and listens on a given port.
# Arguments
- `PORT::Integer`: The port number for the server socket, default is 8000.
"""
function serve(PORT=8000)
	server = listen(PORT)
	println("Listening JuliaCaller on port $PORT")
	while true
		try
			client = accept(server)
			if (@isdefined client)
				handle_client(server, client)
    			else
				close(client)
			end
		catch err
			close(client)
			close(server)
			writeln("Closed connection and server, exiting")
			break
		end
	end
end


