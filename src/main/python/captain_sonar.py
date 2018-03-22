from gamemap import Gamemap
from engineer import Engineering
from first_mate import Systems
import random

class CaptainSonar( object ):
	EASY = "Easy"
	NORMAL = "Normal"
	HARD = "Hard"
	MAX_DAMAGE = 4
	
	def __init__( self ):
		random.seed()
		self.difficulty = self.EASY
		self.stop = False
		self.gamemap = Gamemap()
		self.systems = Systems()
		self.engineering = Engineering()
		# N,S,E,W,Mine,Surface
		self.player_movements = []
		self.sub_damage = 0
		self.possible_player_locations = []
		self.debug = True
		
		
	def print_debug( self, txt ):
		if self.debug:
			print "[DEBUG] %s" % txt
		
		
	def start( self ):
		print "Captain Sonar AI"
		self.gamemap.load( self.choose_map() )
		print "Placing AI sub on map"
		start_loc = self.gamemap.get_random_location()
		start_loc.visited = True
		self.gamemap.current = start_loc
		self.gamemap.print_map()
		self.difficulty = self.choose_difficulty()
		print "Starting game..."
		while not self.stop:
			self.get_player_direction()
			if self.check_gameover():
				continue
			raw_input( "Press Enter when ready for next turn" )
			self.do_comp_turn()
			
			
	def choose_map( self ):
		print "Loading default map"
		#return "../resources/blank_map.txt"
		return "../resources/alpha_map.txt"
			
		
	def choose_difficulty( self ):
		print "Choose AI difficulty:"
		print "1) Easy"
		inp = raw_input( "> " ).strip().lower()
		if inp in [ "1", "easy" ]:
			return self.EASY
		elif inp in [ "2", "normal" ]:
			return self.NORMAL
		elif inp in [ "3", "hard" ]:
			return self.HARD
		return self.EASY
	
		
	def get_player_direction( self ):
		while True:
			inp = raw_input( "Direction and systems: " ).strip()
			parts = inp.split( " " )
			handled_dir = False
			for inp in parts:
				self.print_debug( "Handling '%s'" % inp )
				if inp.lower() in [ "n", "north" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.SOUTH:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.NORTH )
					handled_dir = True
				elif inp.lower() in [ "s", "south" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.NORTH:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.SOUTH )
					handled_dir = True
				elif inp.lower() in [ "e", "east" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.WEST:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.EAST )
					handled_dir = True
				elif inp.lower() in [ "w", "west" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.EAST:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.WEST )
					handled_dir = True
				elif inp.lower().startswith( "surf" ):
					self.player_movements.append( Gamemap.SURFACE )
					handled_dir = True
				elif inp.lower() in [ 'm', 'mine' ]:
					# Player has dropped a mine
					print "You have dropped a mine"
					self.player_movements.append( Gamemap.MINE )
				elif inp.lower() in [ 'tm', 'triggermine', 'detonate' ]:
					# Player has detonated a mine
					self.player_detonate_mine()
				elif inp.lower() in [ 't', 'tor', 'torpedo' ]:
					# Player has launched a torpedo
					self.player_activate_torpedo()
				elif inp.lower() in [ 'd', 'dro', 'drone' ]:
					# Player is asking for a specific quadrant
					self.player_activate_drone()
				elif inp.lower().startswith( 'son' ):
					# Player should receive one true and one false piece of information (row, column, sector)
					self.player_activate_sonar()
				elif inp.lower().startswith( 'sil' ):
					# Player activating silence mode
					self.player_movements.append( Gamemap.SILENCE )
					self.player_activate_silence()
				elif inp.lower().startswith( 'sc' ):
					# Player activating scenario system
					self.player_movements.append( Gamemap.SCENARIO )
					self.player_activate_scenario()
			if not self.stop and not handled_dir:
				print "Invalid direction or no direction given"
				continue
			break
		
		
	def player_activate_torpedo( self ):
		while True:
			xy = raw_input( "Where is the impact (xy)? " ).strip().lower()
			if xy == 'cancel':
				break
			xy = xy.replace( " ", "" )
			x = xy[ 0 ]
			y = xy[ 1: ]
			x = ord(x) - ord('a')
			try:
				y = int(y) - 1
			except:
				print "Invalid Y coordinate - must be a number"
				continue
			if self.handle_impact_at( x, y ):
				break
		
		
	def player_activate_drone( self ):
		while True:
			sector = raw_input( "When sector? " )
			try:
				sector = int(sector)
			except:
				print "Invalid sector"
				continue
			sector -= 1  # sector must be zero-based
			if self.gamemap.get_sector() == sector:
				print "AI Responds with YES"
			else:
				print "AI Responds with NO"
			break
				
	
	def player_activate_sonar( self ):
		# AI must give one true and one false piece of information
		sector = self.gamemap.get_sector()
		trues = {
			'Row': str(self.gamemap.current.y + 1),
			'Col': chr(self.gamemap.current.x + ord('A')),
			'Sector': str(sector + 1)
		}
		falses = {
			"Row": [ str(i + 1) for i in xrange( len(self.gamemap.map) ) if i != self.gamemap.current.y ],
			'Col': [ chr(i + ord('A')) for i in xrange( len(self.gamemap.map) ) if i != self.gamemap.current.x ],
			'Sector': [ str(i + 1) for i in xrange( (len(self.gamemap.map) / 5) * (len(self.gamemap.map) / 5) ) if i != sector ]
		}
		true_cat = random.choice( trues.keys() )
		false_cat = None
		while True:
			false_cat = random.choice( falses.keys() )
			if false_cat == true_cat:
				continue
			break
		false_choice = random.choice( falses[ false_cat ] )
		choices = [ "%s: %s" % (true_cat, trues[ true_cat ]), "%s: %s" % (false_cat, false_choice) ]
		self.print_debug( "%s: %s (TRUE)" % (true_cat, trues[ true_cat ] ) )
		self.print_debug( "%s: %s (FALSE)" % (false_cat, false_choice ) )
		random.shuffle( choices )
		for c in choices:
			print c
	
	
	def player_activate_silence( self ):
		print "Player activates silence!"
	
	
	def player_activate_scenario( self ):
		print "Player activates scenario system!"
		
	
	def player_detonate_mine( self ):
		while True:
			inp = raw_input( "Detonate mine at xy: " ).strip().lower()
			if inp == 'cancel':
				break
			xy = xy.replace( " ", "" )
			x = xy[ 0 ]
			y = xy[ 1: ]
			x = ord(x) - ord('a')
			if x < 0 or x >= len(self.gamemap.map):
				print "Invalid X coordinate"
				continue
			try:
				y = int(y) - 1
			except:
				print "Invalid Y coordinate - must be a number"
				continue
			if self.handle_impact_at( x, y ):
				break
	
	
	def handle_impact_at( self, x, y ):
		print "Handling impact at [%d, %d]" % (x, y)
		impactat = self.gamemap.get( [x,y] )
		if impactat is None:
			print "Invalid coordinate"
			return False
		neighbors = self.gamemap.get_neighbors( [x,y] )
		# Destroy any mines at 'impactat' location
		impactat.mine = False
		# Check if direct hit on AI sub
		if impactat == self.gamemap.current:
			print "DIRECT HIT!"
			self.sub_damage += 2
		elif self.gamemap.current in neighbors:
			print "INDIRECT HIT!"
			self.sub_damage += 1
		else:
			print "ALL CLEAR!"
		if self.sub_damage >= self.MAX_DAMAGE:
			print "AI Sub Sank!"
			self.stop = True
		return True
	
	
	def do_comp_turn( self, silence=False ):
		self.analyze_player_map()
		d = self.comp_choose_direction()
		if not silence or self.debug:
			self.print_debug( "AI Sub moving %s" % d )
		n = self.gamemap.get_in_dir( d )
		n.visited = True
		self.gamemap.current = n
		self.gamemap.print_map()
		self.comp_charge_system()
		self.comp_damage_system( d )
		if self.check_gameover():
			return
		self.comp_activate_system()
		if not silence or self.debug:
			print "\n==================\nAI Sub Moves %s\n==================\n" % d
		
		
	def analyze_player_map( self ):
		""" Attempt to find the player.  Populate the possible_player_locations list. """
		self.possible_player_locations = []
		for y in xrange( self.gamemap.height ):
			for x in xrange( self.gamemap.width ):
				node = self.gamemap.get( (x, y) )
				if self.is_possible_player_location( x, y ):
					self.possible_player_locations.append( (x,y) )
		self.print_debug( "Found %d possible player locations" % len(self.possible_player_locations) )
		#for loc in self.possible_player_locations:
			#self.print_debug( "   %s" % str(loc) )
	
	
	def is_possible_player_location( self, x, y ):
		loc = (x, y)
		expected_sector = None
		sonar_info = None
		for dir in self.player_movements[::-1]:
			if dir.startswith( Gamemap.DRONE ):
				# Sonar(<sector>=YES|NO)
				d = dir[ (dir.find("(") + 1) : dir.find(")") ]
				sector, value = d.split( "=" )
				if value == "yes":
					expected_sector = int(sector)
				else:
					expected_sector = None
			elif dir.startswith( Gamemap.SONAR ):
				# (row=%s, col=%s, sector=%s)
				d = dir[ (dir.find("(") + 1) : dir.find(")") ]
				rowstr, colstr, sectorstr = d.split( "," )
				sonar_info = { 
					'row': rowstr.strip().split( "=" )[ 1 ],
					'col': colstr.strip().split( "=" )[ 1 ],
					'sector': sectorstr.strip().split( "=" )[ 1 ]
				}
			elif dir == Gamemap.MINE:
				pass
			elif dir == Gamemap.SILENCE:
				# Player could have moved 0-4 spaces in a single direction
				pass
			elif dir == Gamemap.SCENARIO:
				pass
			else:
				ret = True
				opp_dir = self.gamemap.get_opposite_direction( dir )
				loc = self.gamemap.get_coord_in_dir( opp_dir, loc )
				node = self.gamemap.get( loc )
				if node is None:
					ret = False
				if ret and expected_sector is not None and not self.gamemap.is_in_sector( expected_sector, loc );
					ret = False
				if ret and sonar_info is not None:
					# Only one of the info is true
					row_true = False
					col_true = False
					sec_true = False
					for key in [ 'row', 'col', 'sector' ]:
						if sonar_info[ key ] != "":
							if key == 'row' and int(sonar_info[ key ]) == loc[ 1 ]:
								row_true = True
							elif key == 'col' and int(sonar_info[ key ]) == loc[ 0 ]:
								col_true = True
							elif key == 'sector' and self.gamemap.is_in_sector( int(sonar_info[ key ]), loc ):
								sec_true = Trues
					# if all are false, then this loc cannot be where the player is
					if not row_true and not col_true and not sec_true:
						ret = False
				# Drone and Sonar info is only valid for one movement
				expected_sector = None
				sonar_info = None
				if not ret:
					return ret
		return True
	
	
	def comp_choose_direction( self ):
		if self.difficulty == self.EASY:
			return self.comp_choose_direction_easy()
		elif self.difficulty == self.NORMAL:
			return self.comp_choose_direction_normal()
		elif self.difficulty == self.HARD:
			return self.comp_choose_direction_hard()
		return Gamemap.SURFACE
		
		
	def comp_choose_direction_easy( self ):
		# Determine valid directions
		options = []
		for dir in Gamemap.DIRS:
			node = self.gamemap.get_in_dir( dir )
			if node is not None and not node.visited and not node.island:
				options.append( dir )
		if len(options) == 0:
			return Gamemap.SURFACE
		return random.choice( options )
	
	
	def comp_choose_direction_normal( self ):
		options = []
		for dir in Gamemap.DIRS:
			node = self.gamemap.get_in_dir( dir )
			if node is not None and not node.visited and not node.island:
				options.append( dir )
		if len(options) == 0:
			return Gamemap.SURFACE
		# TODO Choose best direction based on possible player locations
		quadrant_locations = self.get_most_probable_quadrant_player_location()
		dir = self.get_direction_toward_player_locations( quadrant_locations )
		return dir
	
	
	def comp_choose_direction_hard( self ):
		return self.comp_choose_direction_normal()
		
		
	def comp_charge_system( self ):
		priority = [ 
			Systems.TORPEDO,
			Systems.MINE,
			Systems.SONAR,
			Systems.SILENCE,
			Systems.DRONE,
			#Systems.SCENARIO,
		]
		for system in priority:
			if not self.systems.is_charged( system ):
				self.systems.charge( system )
				self.print_debug( "AI Charging %s" % str(system) )
				break
		
		
	def comp_damage_system( self, dir ):
		comp = self.engineering.get_random_undamaged_component( dir )
		self.print_debug( "AI Damaging %s" % str(comp) )
		if self.engineering.damage( comp ):
			  self.sub_damage += 1
		
		
	def comp_activate_system( self ):
		potential_systems = []
		if True or self.difficulty == self.EASY:
			potential_systems += [ 
				Systems.TORPEDO,
				Systems.MINE,
				Systems.DRONE,
				Systems.SONAR,
				Systems.SILENCE,
				#Systems.SCENARIO,
				Systems.TRIGGER_MINE
			]
		for system in potential_systems:
			if self.systems.is_charged( system ) and self.should_activate_system( system ):
				if self.use_system( system ):
					break
	
	
	def should_activate_system( self, what ):
		if system == Systems.TORPEDO:
			return len(self.possible_player_locations) < 10
		elif system == Systems.MINE:
			return True
		elif system == Systems.SONAR:
			return True
		elif system == Systems.SILENCE:
			return True
		elif system == Systems.DRONE:
			return True
		elif system == Systems.CUSTOM:
			return False
		elif system == Systems.TRIGGER_MINE:
			return True
		return False
		
		
	def use_system( self, system ):
		print "AI using system %s" % system
		if system == Systems.TORPEDO:
			self.ai_fire_torpedo()
		elif system == Systems.MINE:
			self.ai_drop_mine()
		elif system == Systems.SONAR:
			self.ai_use_sonar()
		elif system == Systems.SILENCE:
			self.ai_use_silence()
		elif system == Systems.DRONE:
			self.ai_use_drone()
		elif system == Systems.CUSTOM:
			self.ai_use_scenario()
		elif system == Systems.TRIGGER_MINE:
			self.ai_trigger_mine()
		self.systems.reset( system )
		return False
		
	
	def ai_fire_torpedo( self ):
		# Only fire the torpedo if the AI has a reasonable guess of where the player's sub is
		if len(self.possible_player_locations) == 0:
			print "Huh, AI has no possible player sub locations.  That's not right... Are you cheating?"
			return
		coord = random.choice( self.possible_player_locations )
		impactat = self.gamemap.get( coord )
		while True:
			print "AI fired torpedo, impact at %s" % (self.gamemap.format_coord( impactat ))
			print "1) DIRECT HIT!"
			print "2) INDIRECT HIT!"
			print "3) ALL CLEAR!"
			inp = raw_input( "> " ).strip().lower()
			if inp == "1":
				break
			elif inp == "2":
				break
			elif inp == "3":
				break
			else:
				print "Invalid option"
			
	
	def ai_drop_mine( self ):
		neighbors = self.gamemap.get_neighbors()
		neighbors = [ n for n in neighbors if not n.island and not n.mine ]
		neighbor = random.choice( neighbors )
		neighbor.mine = True

	
	def ai_trigger_mine( self ):
		# Choose mine
		mines = self.gamemap.get_nodes_with_mines()
		if len(mines) == 0:
			return
		node = random.choice( mines )
		while True:
			print "AI triggering mine at %s" % self.gamemap.format_coord( [node.x, node.y] )
			print "1) DIRECT HIT!"
			print "2) INDIRECT HIT!"
			print "3) ALL CLEAR!"
			inp = raw_input( "> " ).strip().lower()
			if inp == "1":
				break
			elif inp == "2":
				break
			elif inp == "3":
				break
			else:
				print "Invalid option"
				
	
	def ai_use_sonar( self ):
		while True:
			print "AI is activating sonar!"
			print "You must tell one true and one false regarding row, column, and sector"
			print "Enter a value for two of the following three questions (one value must be true and one value must be false).  Leave one of the questions blank."
			row = raw_input( "Row? " ).strip().lower()
			if row != "":
				try:
					row = int(row) - 1
				except:
					print "Invalid value"
					continue
				if row < 0 or row >= len(self.gamemap.map):
					print "Invalid row number"
					continue
			col = raw_input( "Column? " ).strip().lower()
			if col != "":
				if len(col) > 1:
					print "Invalid column letter"
					continue
				col = ord(col) - ord("a")
				if col < 0 or col >= len(self.gamemap.map):
					print "Invalid column letter"
					continue
			sector = ""
			if row == "" or col == "":
				sector = raw_input( "Sector? " ).strip().lower()
				try:
					sector = int(sector) - 1
				except:
					print "Invalid sector"
					continue
			blanks = [ v for v in [ row, col, sector ] if v == "" ]
			if len(blanks) != 1:
				print "You must answer 2 questions and leave one blank"
				continue
			self.player_movements.append( "%s(row=%s, col=%s, sector=%s)" % (Gamemap.SONAR, str(row), str(col), str(sector)) )
	
	
	def ai_use_drone( self ):
		while True:
			print "AI is activating a drone!"
			sectors = {}
			for coord in self.possible_player_locations:
				s = self.gamemap.get_sector( coord )
				if s not in sectors.keys():
					sectors[ s ] = 0
				sectors[ s ] += 1
			if len(sectors) > 0:
				# Find the sector with the most possible locations
				sector = max(sectors, key=sectors.get)
			else:
				sector = random.choice( [ 1, 2, 3, 4 ] )
			inp = raw_input( "Are you in Sector %d? " % sector ).strip().lower()
			answer = "no"
			if inp in [ 'y', 'yes' ]:
				answer = 'yes'
			self.player_movements.append( "%s(%d=%s)" % (Gamemap.DRONE, sector, answer) )
	
	
	def ai_use_silence( self ):
		print "AI is activating silence!"
		# AI may move up to 4 spaces
		num_movements = random.choice( range( 5 ) )
		for i in xrange( num_movements ):
			self.do_comp_turn( silence=True )
	
	def ai_use_scenario( self ):
		print "AI is activating scenario-specific system.  This is not yet implemented."
	
	
	def check_gameover( self ):
		if self.sub_damage == self.MAX_DAMAGE:
			print "AI Sub has taken too much damage!"
			self.stop = true
		if self.stop:
			print "GAME OVER"
		return self.stop
		
		
	def get_most_probable_quadrant_player_location( self ):
		quadrant_locations = []
		return quadrant_locations
	
	
	def get_direction_toward_player_locations( self, quadrant_locations ):
		dir = self.NORTH
		return dir
		
		
if __name__ == "__main__":
	cs = CaptainSonar()
	cs.start()
	